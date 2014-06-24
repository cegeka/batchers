##Integration testing in Spring Batch

Like all applications we develop at Cegeka, we use TDD as our preferred methodology. So, we want to make sure that our code works fine both by both unit tests and integration tests. After our [first](http://blog.cegeka.be/2014/06/use-spring-batch/) and [second](http://blog.cegeka.be/2014/06/todo) blog post, it is now time to dive deeper into Integration testing with Spring Batch.

For our integration tests, we first created a base class called __AbstractBatchIntegrationTest__ 

```java

	@RunWith(TaxCalculatorSpringJUnitClassRunner.class)
	@ActiveProfiles("test")
	@ContextConfiguration(classes = {EmployeeJobTestConfig.class, EmployeeJobConfigSingleJvm.class,
	        EmployeeGeneratorTestConfig.class, WebserviceCallConfig.class, PropertyPlaceHolderConfig.class,
	        InfrastructureConfig.class, PersistenceConfig.class})
	public abstract class AbstractBatchIntegrationTest {
	    @Autowired
	    private DataSource dataSource;
	
	    @After
	    public void clearJobTables() throws SQLException {
	        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
	        databasePopulator.setScripts(
	                new ClassPathResource("org/springframework/batch/core/schema-truncate-hsqldb.sql")
	        );
	        DatabasePopulatorUtils.execute(databasePopulator, this.dataSource);
	    }
	}

```

A lot of things happen here, let's go through this step by step:

* An important thing to note is that we need to have a clean slate for each test and for this we need to clear the records generated after each job run.
The reason we choose this solution is because we could not use transaction rollback as we want to make sure that Spring Batch persisted the status of the job execution.
To start with a clean slate each time, we implemented the __cleanJobTables__ method that we execute after each test case.
* We want to point out that we repeat the __PersistenceConfig__ in the _ContextConfiguration_ as the last element. Why you ask? Well, if we don't do that, we end up with two Transaction Managers: a _DataSourceTransactionManager_ and our own _JpaTransactionManager_. And you don't want two Transaction Managers... We debugged through the Spring Core and it all boils down to the fact that our _transactionManager_ bean definition from PersistenceConfig is overriden with one provided by Spring Batch if we don't specify our  __PersistenceConfig__ as last.
* Another important aspect is that we defined our own class runner, __TaxCalculatorSpringJUnitClassRunner__ so we select an environment configured for testing purposes.

```java
	
	public class TaxCalculatorSpringJUnitClassRunner extends SpringJUnit4ClassRunner {
	    public TaxCalculatorSpringJUnitClassRunner(Class<?> clazz) throws InitializationError {
	        super(clazz);
	    }
	
	    @Override
	    protected TestContextManager createTestContextManager(Class<?> clazz) {
	        System.setProperty("APP_ENV", "default");
	        return super.createTestContextManager(clazz);
	    }
	}
```



When calculating the tax for employees, two parameters are relevant: the _year_ and the _month_. In our __[EmployeeBatchJobITest](https://github.com/cegeka/batchers/blob/master/taxcalculator/taxcalculator-batch/src/test/java/be/cegeka/batchers/taxcalculator/batch/integration/EmployeeBatchJobITest.java)__, 
we test the happy path and different corner cases for our batch job.

We can easily start testing the job thanks to _Spring Batch's JobLauncherTestUtils_ that we can autowire.

```java

	public class EmployeeBatchJobITest extends AbstractBatchIntegrationTest {    
	    private static final Long YEAR = 2014L;
	    private static final Long MONTH = 1L;
	
	    @Autowired
	    private JobLauncherTestUtils jobLauncherTestUtils;
		private JobParameters jobParams;
		
		@Before
		public void setUp(){
		   Map<String, JobParameter> jobParamsMap = new HashMap<>();
	        jobParamsMap.put("month", new JobParameter(MONTH, false));
	        jobParamsMap.put("year", new JobParameter(YEAR, false));
	
	        jobParams = new JobParameters(jobParamsMap);}
		
		@Test
	    public void jobLaunched_HappyPath() throws Exception {
	        haveEmployees(1);
	        respondOneTimeWithSuccess();
	        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParams);
	
	        assertThat(jobExecution.getStatus()).isEqualTo(COMPLETED);
	        verifyJob(jobExecution);
	    }

		private void verifyJob(JobExecution jobExecution) {
	        mockServer.verify();
	
	        jobExecution.getStepExecutions()
	                .forEach(se -> se.getFailureExceptions()
	                        .forEach(t -> assertThat(t).isNotInstanceOf(AssertionError.class)));
	    }
}
```

An important thing to with Spring Batch Integration testing is to make sure that a Step didn't fail because of an __AssertionError__. In our Integration tests, we mock out the Tax Payment Webservice using the _MockRestServiceServer_. If more rest calls than expected arrive at the _MockRestServiceServer_, it throws an _AssertionError_ which will be catched by Spring Batch and which is then kept as a FailureException for that Step.
So, it seems that your job fails for the correct reason while in fact it fails because of an __AssertionError__. We solved this by using the _verifyJob_ method above.


For testing specific __Job Steps__ in detail, we can use the same _JobLauncherTestUtils_ like we do here:
```java

	public class TaxCalculationStepITest extends AbstractBatchIntegrationTest {
	    @Test
	    public void taxCalculationStep_noWork() throws Exception {
	        JobParameters jobParameters = new JobParametersBuilder()
	                .addLong("year", 2014L, true)
	                .addLong("month", 5L, true)
	                .toJobParameters();
	
	        JobExecution jobExecution = jobLauncherTestUtils.launchStep(EmployeeJobConfigSingleJvm.TAX_CALCULATION_STEP, jobParameters);
	
	        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
	        assertThat(taxCalculationRepository.find(2014, 5, 1L)).isEmpty();
	    }
```

You want to know more of how we do Integration Testing with Spring Batch? Do not hesitate to take a look at __[EmployeeBatchJobITest](https://github.com/cegeka/batchers/blob/master/taxcalculator/taxcalculator-batch/src/test/java/be/cegeka/batchers/taxcalculator/batch/integration/EmployeeBatchJobITest.java)__!

#That's it for today!
Please check out our future blog post about integration testing in Spring Batch!