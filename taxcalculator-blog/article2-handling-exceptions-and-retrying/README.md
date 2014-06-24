##Handling exceptions and retrying

In our introductory post about Spring Batch we found that some operations might depend on external web services so it is not simple to restart the process only for the employees with failed operations.

To automate the retry of such operations, Spring Batch has the __RetryOperations__ strategy and the simplest general purpose implementation of it is __RetryTemplate__. It could be used like this:

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

In this example, inside of our __RetryTemplate__ , the decision to retry of fail is determined by a __SimpleRetryPolicy__ that just allows a retry on any named list of exceptions types (_TaxWebServiceNonFatalException_), up to a fixed number of times (_maxAttempts_).

It also could have a list of "fatal" exceptions that should never be retried, and this list overrides the retryable list so that it can be used to give finer control over the retry behavior.

When retrying after a transient failure it often helps to wait a bit before trying again, because usually the failure is caused by some problem that will only be resolved by waiting. 

If it fails, the __RetryTemplate__ can pause execution according to the a __BackoffPolicy__ in place.

The policies provided by Spring Batch out of the box all use __Object.wait()__. A common use case is to backoff with an exponentially increasing wait period, to avoid two retries getting into lock step and both failing. 

For this purpose Spring Batch provides the __ExponentialBackoffPolicy__. The configuration is straightforward and clear.

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

```

The problem with webservices is the fact that they are not transactional or idempotent. This is why we created __TaxPaymentWebServiceFacade__ which wraps the actual web service but makes it idempotent. If the first invocation fails the second invocation tries again. But if the first invocation finishes successfully the second invocation does not hit the web service and returns the same result. So basically it retries until it gets a 'good' result.

Now it's time to use the  __RetryTemplate__ we defined earlyer to call the web service:

```java

			@Override
			public TaxWebserviceCallResult process(TaxCalculation taxCalculation) throws Exception {

				RetryTemplate retryTemplate = retryConfig.createRetryTemplate();
				Callable<Void> callable = () -> retryTemplate.execute(doWebserviceCallWithRetryCallback(taxCalculation));

				TaxWebserviceCallResult taxWebserviceCallResult = taxPaymentWebServiceFacade.callTaxService(taxCalculation, callable);

				return taxWebserviceCallResult;
			}
```

The __callable__ wraps the retry template hiding the retries from the calling object. This decouples the retry from our actual web service call.

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

Now, let's talk a little about skippable exception and transaction.

A step in our job is a web service call, generate and send paycheck ("wsCallAndGenerateAndSendPaycheckStep"):

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
                .listener(createMonthlyTaxForEmployeeListener)
                .listener(maxConsecutiveExceptionsSkipPolicy)
                .listener(failedStepStepExecutionListener)
                .listener(singleJVMJobProgressListener)
                .allowStartIfComplete(true)
                .taskExecutor(taskExecutor)
                .build();
    }
```

and this means that __ItemWriter__ receives a list of items to write and if a skippable exception is thrown, Spring Batch attempts to determine which item actually caused the exception so only that item is skipped. There is no rollback for __TaxWebServiceNonFatalException.class__ and __EmailSenderException.class_ and each item is then reprocessed and the write is attemped again. This allows only the item with the error to be skipped instead of needing to skip the entire chunk.



Please check out our future blog post about integration testing in Spring Batch!
