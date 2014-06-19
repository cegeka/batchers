package be.cegeka.batchers.taxcalculator.batch.integration;

import be.cegeka.batchers.taxcalculator.application.config.EmployeeGeneratorTestConfig;
import be.cegeka.batchers.taxcalculator.application.config.WebserviceCallConfig;
import be.cegeka.batchers.taxcalculator.application.infrastructure.TaxCalculatorSpringJUnitClassRunner;
import be.cegeka.batchers.taxcalculator.batch.config.EmployeeJobTestConfig;
import be.cegeka.batchers.taxcalculator.batch.config.singlejvm.EmployeeJobConfigSingleJvm;
import be.cegeka.batchers.taxcalculator.infrastructure.config.InfrastructureConfig;
import be.cegeka.batchers.taxcalculator.infrastructure.config.PersistenceConfig;
import be.cegeka.batchers.taxcalculator.infrastructure.config.PropertyPlaceHolderConfig;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import javax.sql.DataSource;
import java.sql.SQLException;

@RunWith(TaxCalculatorSpringJUnitClassRunner.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {EmployeeJobTestConfig.class, EmployeeJobConfigSingleJvm.class,
        EmployeeGeneratorTestConfig.class, WebserviceCallConfig.class, PropertyPlaceHolderConfig.class,
        InfrastructureConfig.class, PersistenceConfig.class})
public abstract class AbstractBatchIntegrationTest {

    @Autowired
    private DataSource dataSource;

    @BeforeClass
    public static void fixDateTimeToWhenJesusWasBorn() {
        DateTimeUtils.setCurrentMillisFixed(100L);
    }

    @AfterClass
    public static void resetDateTime() {
        DateTimeUtils.currentTimeMillis();
    }

    @After
    public void clearJobTables() throws SQLException {
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        databasePopulator.setScripts(
                new ClassPathResource("org/springframework/batch/core/schema-truncate-hsqldb.sql")
        );
        DatabasePopulatorUtils.execute(databasePopulator, this.dataSource);
    }

}
