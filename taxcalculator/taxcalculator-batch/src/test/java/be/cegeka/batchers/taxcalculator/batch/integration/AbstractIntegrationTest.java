package be.cegeka.batchers.taxcalculator.batch.integration;

import be.cegeka.batchers.taxcalculator.application.config.ApplicationTestContext;
import be.cegeka.batchers.taxcalculator.application.config.PropertyPlaceHolderConfig;
import be.cegeka.batchers.taxcalculator.batch.EmployeeJobConfig;
import be.cegeka.batchers.taxcalculator.application.domain.EmployeeRepository;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {EmployeeJobTestConfig.class, EmployeeJobConfig.class, ApplicationTestContext.class, PropertyPlaceHolderConfig.class})
public abstract class AbstractIntegrationTest {

    @Autowired
    private MockResetter mockResetter;

    @BeforeClass
    public static void fixDateTimeToWhenJesusWasBorn() {
        DateTimeUtils.setCurrentMillisFixed(100L);
    }

    @AfterClass
    public static void resetDateTime() {
        DateTimeUtils.currentTimeMillis();
    }

    @After
    public void resetMocks() {
        mockResetter.resetMocks();
    }
}
