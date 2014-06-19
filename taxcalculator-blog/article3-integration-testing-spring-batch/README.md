##Integration testing in Spring Batch

For our needs we created a base for our integration tests __AbstractBatchIntegrationTest__

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

The important thing here is that we need to have a clean slate for each test and for this we need to clear the records generated after each job run.
The reason we choose this solution is because we could not use transaction rollback as spring batch needs to make sure it persisted the status of the job execution.
We do that in the __cleanJobTables__ method that we execute after each test case.

Another important aspect is that we defined our own class runner, __TaxCalculatorSpringJUnitClassRunner__ so we select an environment configured for testing purposes

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

We tested different succeding and failing test cases for our job that is automatically injected into Spring's __JobLauncherTestUtils__.
The job is run with two parameters that we pass along:
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
```