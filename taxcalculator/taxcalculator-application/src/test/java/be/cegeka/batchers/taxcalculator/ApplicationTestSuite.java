package be.cegeka.batchers.taxcalculator;

import be.cegeka.batchers.taxcalculator.domain.EmployeeGeneratorTest;
import be.cegeka.batchers.taxcalculator.domain.EmployeeRepositoryTest;
import be.cegeka.batchers.taxcalculator.domain.EmployeeServiceTest;
import be.cegeka.batchers.taxcalculator.service.RunningTimeServiceIntegrationTest;
import be.cegeka.batchers.taxcalculator.service.RunningTimeServiceTest;
import be.cegeka.batchers.taxcalculator.service.TaxCalculatorServiceTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import static org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(value = {
        EmployeeGeneratorTest.class,
        EmployeeRepositoryTest.class,
        EmployeeServiceTest.class,
        RunningTimeServiceIntegrationTest.class,
        RunningTimeServiceTest.class,
        TaxCalculatorServiceTest.class
})
public class ApplicationTestSuite {
}
