##Handling exceptions and retrying
In our [first post](http://blog.cegeka.be/2014/06/use-spring-batch/) about Spring Batch we described how to use Spring Batch for a tax calculation process:
 
* we first do the taxcalculation, 
* then we pay those taxes via a webservice and
* last not but least we generate a report in PDF format. 

In real life you can expect things to wrong sometimes.  The thing that in our case can go wrong is the payment of the taxes via the webservice. In this blog post we describe two ways how to handle these kind of exceptions:

* [Spring's retry template](#retry-template)
* [Handling exceptions in the Step configuration](#step-configuration)

<a name="retry-template"></a>Spring's retry template
----------------------------------------------------
Let's assume it's the end of the month and all the companies are paying their taxes. The server get's too many requests and it returns a Http Status Code 503. The same operation may succeed only a couple of seconds later as resources become available again.

In such a case, Spring's retry library comes in handy. It in fact just implements the __RetryOperations__ strategy and the simplest general purpose implementation is the __RetryTemplate__. It could be used like this:

```java

		@Configuration
		public class RetryConfig {

			@Value("${employeeJob.taxProcessor.retry.initialInterval:100}")
			private long initialInterval = 100;

			@Value("${employeeJob.taxProcessor.retry.maxAttemptsPerEmployee:3}")
			private int maxAttempts = 3;

			public RetryTemplate createRetryTemplate() {
				Map<Class<? extends Throwable>, Boolean> exceptions = new HashMap<>();
				exceptions.put(TaxWebServiceNonFatalException.class, true);

				RetryTemplate template = new RetryTemplate();
				SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(maxAttempts, exceptions);
				template.setRetryPolicy(retryPolicy);

				ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
				backOffPolicy.setInitialInterval(initialInterval);
				template.setBackOffPolicy(backOffPolicy);

				return template;
			}
		}
```

In this example, inside of our __createRetryTemplate__ , the decision to retry is determined by a __SimpleRetryPolicy__ that allows a retry using a named list of exceptions types (in our case _TaxWebServiceNonFatalException_), and this up to a fixed number of times (_maxAttempts_).

When retrying after a temporary failure it often helps to wait a little bit before trying again (in our case having enough resources again on the Tax Payment Webservice backend). If an exception occurs, the __RetryTemplate__ can pause the execution according to the specified __BackoffPolicy__. The policies provided by Spring Batch out of the box all use __Object.wait()__. A common use case is to backoff with an exponentially increasing wait period, which can be easily accomplished using the __ExponentialBackOffPolicy__. 

###Webservices transactional?
 You don't want to pay taxes to the government twice for the same month, do you? So, the idempotence of this webservice is quite important. The problem with webservices (and of course the one we need to call), is the fact that they may not be transactional or idempotent. 
 This is why we created __TaxPaymentWebServiceFacade__ which wraps the actual web service but makes it (almost) idempotent. If the first invocation fails, the second invocation tries again. But if the first invocation finishes successfully the second invocation does not hit the web service and returns the same result. So basically it retries until it gets a 'successfull' result.

Now it's time to use the  __RetryTemplate__ we defined earlyer to call the web service:


```java
 
		@Component
		@Import(RetryConfig.class)
		public class CallWebserviceProcessor implements ItemProcessor<TaxCalculation, TaxWebserviceCallResult> {

			@Autowired
			private TaxPaymentWebService taxPaymentWebService;

			@Autowired
			private TaxPaymentWebServiceFacade taxPaymentWebServiceFacade;

			@Autowired
			private RetryConfig retryConfig;

			@Override
			public TaxWebserviceCallResult process(TaxCalculation taxCalculation) throws Exception {

				RetryTemplate retryTemplate = retryConfig.createRetryTemplate();
				Callable<Void> callable = () -> retryTemplate.execute(doWebserviceCallWithRetryCallback(taxCalculation));

				TaxWebserviceCallResult taxWebserviceCallResult = taxPaymentWebServiceFacade.callTaxService(taxCalculation, callable);

				return taxWebserviceCallResult;
			}
```

The __callable__ wraps the retry template with the new Java 8 syntax. This way, the __TaxPaymentWebServiceFacade__ does not need to know anything about Spring's Retry Template.

```java

			private RetryCallback<Void, TaxWebServiceException> doWebserviceCallWithRetryCallback(TaxCalculation taxCalculation) {
				return new RetryCallback<Void, TaxWebServiceException>() {
					@Override
					public Void doWithRetry(RetryContext context) throws TaxWebServiceException {
						taxPaymentWebService.doWebserviceCallToTaxService(taxCalculation.getEmployee(), taxCalculation.getTax());
						return null;
					}
				};
			}
		}
```


<a name="step-configuration"></a>Handling exceptions in the Step configuration
------------------------------------------------------------------------------
The second step in our job is a calling the Tax Payment Webservice, and if that succeeds, generating and sending the employees paycheck:

```java
 
	protected Step wsCallAndGenerateAndSendPaycheckStep(String stepName) {
        CompositeItemProcessor<TaxCalculation, PayCheck> compositeItemProcessor = new CompositeItemProcessor<>();
        compositeItemProcessor.setDelegates(Arrays.asList(
                callWebserviceProcessor,
                sendPaycheckProcessor
        ));

        return stepBuilders.get(stepName)
                .<TaxCalculation, PayCheck>chunk(5)
                .faultTolerant()
                .skipPolicy(maxConsecutiveExceptionsSkipPolicy)
                .noRollback(TaxWebServiceNonFatalException.class)
                .noRollback(EmailSenderException.class)
                .reader(wsCallItemReader)
                .processor(compositeItemProcessor)
                .writer(wsCallItemWriter)
                .listener(maxConsecutiveExceptionsSkipPolicy)
                .listener(failedStepStepExecutionListener)
                .allowStartIfComplete(true)
                .build();
    }
```

We can already handle some failures by the backend thanks to the retry template described above. But what if the Tax Payment Webservice backend keeps throwing errors? If the Tax Payment Webservice fails for 5 consecutive employees, something is wrong really bad and the best thing to do is to stop.

Let's go through the code above step by step:

* First, we need to make our _StepBuilder_ __faultTolerant__ which allows us to specify a _skipPolicy_ and _noRollback_ exceptions
* Second, we need to tell Spring Batch that it should not rollback the chunk for the TaxWebServiceNonFatalException and EmailSenderException exceptions. If one of those exception occurs, we don't care. The next time we run our Job Instance, these items will be retried and hopefully succeed. If we would not have specified the noRollback exceptions, Spring Batch would restart the complete chunk and omit the item that failed.
* Next, we implemented the __MaxConsecutiveExceptionsSkipPolicy__. In this _SkipPolicy_, we simply count the TaxWebServiceNonFatalException and EmailSenderException exceptions that occur after each other. Each time the taxes are paid successfully for a certain employee (which we find out by registering as an __ItemProcessListener__), we reset the counter. And if it fails 5 times in a row, we throw a __SkipLimitExceededException__ telling Spring to stop the job completely. If we would not have specified the skipPolicy, the job would immediately stop.
* And last but not least, we implemented a __FailedStepStepExecutionListener__. This is an implementation of a _StepExecutionListener_ and makes sure that the _Job Instance_ is marked as failed so that we can rerun it later. This is needed because we marked the exceptions as TaxWebServiceNonFatalException and EmailSenderException as skippable.

This all works really fine but there is one big caveat to remember: _you really need to make sure that you implement your ItemReader's carrefully!_ In our case, we want to restart our _Job Instance_ so we need to return all the TaxCalculation objects for which we didn't find a Paycheck in the database for a given year and month. 
Since we are using the JpaPagingItemReader there is one extra thing to keep in mind: the reader is paged and goes to the database per page.An example makes this more clear:

```

	first chunk: first page; no paychecks in DB available => returns items 1 to 5

	second chunk: second page; 5 paychecks in DB (first 5 are not returned) => returns items 11 to 15

```

__This means we skipped items 6 to 10!__  To solve this we need to track the _jobExecutionId_ and select all Tax Calculation records for which we didn't find a Paycheck record in the database for a given year and month and not the current Job Execution. 
This all sounds more difficult than it is: just check out the code and take a look at our [wsCallAndGenerateAndSendPaycheckStep](https://github.com/cegeka/batchers/blob/master/taxcalculator/taxcalculator-batch/src/main/java/be/cegeka/batchers/taxcalculator/batch/config/AbstractEmployeeJobConfig.java)

#That's it for today!
Please check out our future blog post about integration testing in Spring Batch!
