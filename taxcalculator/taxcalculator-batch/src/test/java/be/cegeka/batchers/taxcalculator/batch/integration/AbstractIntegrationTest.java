package be.cegeka.batchers.taxcalculator.batch.integration;

import be.cegeka.batchers.taxcalculator.application.config.EmployeeGeneratorTestConfig;
import be.cegeka.batchers.taxcalculator.application.config.WebserviceCallConfig;
import be.cegeka.batchers.taxcalculator.application.infrastructure.TaxCalculatorSpringJUnitClassRunner;
import be.cegeka.batchers.taxcalculator.batch.config.EmployeeJobConfig;
import be.cegeka.batchers.taxcalculator.batch.config.EmployeeJobTestConfig;
import be.cegeka.batchers.taxcalculator.infrastructure.config.PropertyPlaceHolderConfig;
import org.joda.time.DateTimeUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@RunWith(TaxCalculatorSpringJUnitClassRunner.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {EmployeeJobTestConfig.class, EmployeeJobConfig.class, EmployeeGeneratorTestConfig.class, WebserviceCallConfig.class, PropertyPlaceHolderConfig.class})
public abstract class AbstractIntegrationTest {

    @BeforeClass
    public static void fixDateTimeToWhenJesusWasBorn() {
        DateTimeUtils.setCurrentMillisFixed(100L);
    }

    @AfterClass
    public static void resetDateTime() {
        DateTimeUtils.currentTimeMillis();
    }

}
