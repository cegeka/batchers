package be.cegeka.batchers.taxcalculator.batch.integration;

import be.cegeka.batchers.taxcalculator.batch.EmployeeJobConfig;
import be.cegeka.batchers.taxcalculator.config.ApplicationTestContext;
import be.cegeka.batchers.taxcalculator.config.PersistenceConfig;
import be.cegeka.batchers.taxcalculator.config.PropertyPlaceHolderConfig;
import be.cegeka.batchers.taxcalculator.domain.EmployeeRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {EmployeeJobConfig.class, ApplicationTestContext.class, PropertyPlaceHolderConfig.class, EmployeeJobTestConfig.class})
@DirtiesContext
public abstract class AbstractIntegrationTest {

    @Autowired
    private MockResetter mockResetter;

    @After
    public void resetMocks() {
        mockResetter.resetMocks();
    }
}
