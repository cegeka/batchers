package be.cegeka.batchers.taxcalculator.application.infrastructure;

import be.cegeka.batchers.taxcalculator.application.config.EmployeeGeneratorTestConfig;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;

import javax.transaction.Transactional;

@ContextConfiguration(classes = {EmployeeGeneratorTestConfig.class})
@RunWith(TaxCalculatorSpringJUnitClassRunner.class)
@TransactionConfiguration(defaultRollback = true)
@Transactional
public abstract class IntegrationTest {

}
