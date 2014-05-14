package be.cegeka.batchers.taxcalculator.batch.integration;

import be.cegeka.batchers.taxcalculator.application.config.ApplicationTestContext;
import be.cegeka.batchers.taxcalculator.application.config.PropertyPlaceHolderConfig;
import be.cegeka.batchers.taxcalculator.application.config.AppConfig;
import be.cegeka.batchers.taxcalculator.batch.EmployeeJobConfig;
import org.joda.time.DateTimeUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ComponentScan()
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {EmployeeJobTestConfig.class, EmployeeJobConfig.class, ApplicationTestContext.class, PropertyPlaceHolderConfig.class, AppConfig.class})
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
