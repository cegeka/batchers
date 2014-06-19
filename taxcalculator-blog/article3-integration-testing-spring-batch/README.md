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
We that in the __cleanJobTables__ method that we execute after each test case.

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