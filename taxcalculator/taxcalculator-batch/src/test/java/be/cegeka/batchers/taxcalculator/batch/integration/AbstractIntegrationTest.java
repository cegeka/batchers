package be.cegeka.batchers.taxcalculator.batch.integration;

import be.cegeka.batchers.taxcalculator.application.config.ApplicationTestContext;
import be.cegeka.batchers.taxcalculator.application.config.AppConfig;
import be.cegeka.batchers.taxcalculator.batch.EmployeeJobConfig;
import be.cegeka.batchers.taxcalculator.infrastructure.config.PropertyPlaceHolderConfig;
import org.joda.time.DateTimeUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {EmployeeJobTestConfig.class, EmployeeJobConfig.class, ApplicationTestContext.class, AppConfig.class, PropertyPlaceHolderConfig.class})
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
