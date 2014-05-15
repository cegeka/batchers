package be.cegeka.batchers.taxcalculator.application.infrastructure;

import be.cegeka.batchers.taxcalculator.application.config.EmployeeGeneratorApplicationTestContext;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import javax.transaction.Transactional;

@ContextConfiguration(classes = {EmployeeGeneratorApplicationTestContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback = true)
@Transactional
public abstract class IntegrationTest {
}
