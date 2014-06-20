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

For this purpose Spring Batch provides the __ExponentialBackoffPolicy__.


Now, we can use our __RetryTemplate__ and call the web service:

 ```java
 
		@Component
		@Import(RetryConfig.class)
		public class CallWebserviceProcessor implements ItemProcessor<TaxCalculation, TaxWebserviceCallResult> {
			private static final Logger LOG = LoggerFactory.getLogger(CallWebserviceProcessor.class);

			@Autowired
			private TaxPaymentWebService taxPaymentWebService;

			@Autowired
			private TaxPaymentWebServiceFacade taxPaymentWebServiceFacade;

			@Autowired
			private RetryConfig retryConfig;

			@Override
			public TaxWebserviceCallResult process(TaxCalculation taxCalculation) throws Exception {
				LOG.info("Web service process: " + taxCalculation);

				RetryTemplate retryTemplate = retryConfig.createRetryTemplate();
				Callable<Void> callable = () -> retryTemplate.execute(doWebserviceCallWithRetryCallback(taxCalculation));

				TaxWebserviceCallResult taxWebserviceCallResult = taxPaymentWebServiceFacade.callTaxService(taxCalculation, callable);

				return taxWebserviceCallResult;
			}

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
