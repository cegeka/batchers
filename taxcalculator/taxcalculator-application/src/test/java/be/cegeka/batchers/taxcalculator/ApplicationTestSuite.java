package be.cegeka.batchers.taxcalculator;

import be.cegeka.batchers.taxcalculator.domain.EmployeeGeneratorTest;
import be.cegeka.batchers.taxcalculator.domain.EmployeeRepositoryTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import static org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(value = {EmployeeGeneratorTest.class, EmployeeRepositoryTest.class})
public class ApplicationTestSuite {
}
