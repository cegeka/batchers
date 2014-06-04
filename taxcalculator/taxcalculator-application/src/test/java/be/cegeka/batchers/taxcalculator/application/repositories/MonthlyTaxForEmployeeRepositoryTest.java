package be.cegeka.batchers.taxcalculator.application.repositories;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.domain.EmployeeTestBuilder;
import be.cegeka.batchers.taxcalculator.application.domain.MonthlyTaxForEmployee;
import be.cegeka.batchers.taxcalculator.application.domain.MonthlyTaxForEmployeeTestBuilder;
import be.cegeka.batchers.taxcalculator.application.infrastructure.IntegrationTest;
import be.cegeka.batchers.taxcalculator.application.repositories.EmployeeRepository;
import be.cegeka.batchers.taxcalculator.application.repositories.MonthlyTaxForEmployeeRepository;
import org.joda.money.Money;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static be.cegeka.batchers.taxcalculator.application.domain.EmployeeTestFixture.anEmployee;
import static org.fest.assertions.api.Assertions.assertThat;

@Ignore(value = "HSQL has troubles with nested transactions? Hangs in ResultSetReturnImpl:187")
public class MonthlyTaxForEmployeeRepositoryTest extends IntegrationTest {

    @Autowired
    private MonthlyTaxForEmployeeRepository monthlyTaxForEmployeeRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee gigel;
    private Employee ionel;
    private MonthlyTaxForEmployee gigelJanuary;
    private MonthlyTaxForEmployee gigelFebruary;
    private MonthlyTaxForEmployee ionelJanuary;
    private MonthlyTaxForEmployee ionelFebruary;


    @Test
    public void testSave_UpdatesExistingMonthlyTaxForEmployeeRecord() {
        Employee employee = new EmployeeTestBuilder().build();
        employeeRepository.save(employee);

        //ACT
        MonthlyTaxForEmployee marchFailed = new MonthlyTaxForEmployeeTestBuilder().withEmployee(employee).withYear(2014).withMonth(1).withTax(10.0).withLastErrorMessage("it failed").build();
        monthlyTaxForEmployeeRepository.save(marchFailed);

        MonthlyTaxForEmployee marchSucceeded = new MonthlyTaxForEmployeeTestBuilder().withEmployee(employee).withYear(2014).withMonth(1).withTax(10.0).withAPdf().build();
        MonthlyTaxForEmployee marchSucceededWitId = monthlyTaxForEmployeeRepository.save(marchSucceeded);

        List<MonthlyTaxForEmployee> monthlyTaxForEmployee = monthlyTaxForEmployeeRepository.findByEmployee(employee);

        //ASSERT
        assertThat(monthlyTaxForEmployee).hasSize(1);
        assertThat(monthlyTaxForEmployee.get(0)).isEqualsToByComparingFields(marchSucceededWitId);
    }

    @Test
    public void testFindByEmployee() {
        setupSomeEmployeesAndMonthlyTaxes();

        //ACT
        List<MonthlyTaxForEmployee> monthlyTaxForEmployees = monthlyTaxForEmployeeRepository.findByEmployee(ionel);

        //ASSERT
        assertThat(monthlyTaxForEmployees).containsOnly(ionelJanuary, ionelFebruary);
    }

    @Test
    public void testFind() {
        setupSomeEmployeesAndMonthlyTaxes();

        //ACT
        MonthlyTaxForEmployee monthlyTaxForEmployee = monthlyTaxForEmployeeRepository.find(ionel, 2014, 1);

        //ASSERT
        assertThat(monthlyTaxForEmployee).isEqualsToByComparingFields(ionelJanuary);
    }

    private void setupSomeEmployeesAndMonthlyTaxes() {
        gigel = new EmployeeTestBuilder().build();
        ionel = new EmployeeTestBuilder().build();

        employeeRepository.save(gigel);
        employeeRepository.save(ionel);

        gigelJanuary = new MonthlyTaxForEmployeeTestBuilder().withEmployee(gigel).withYear(2014).withMonth(1).withTax(10.0).build();
        gigelFebruary = new MonthlyTaxForEmployeeTestBuilder().withEmployee(gigel).withYear(2014).withMonth(2).withTax(10.0).build();

        ionelJanuary = new MonthlyTaxForEmployeeTestBuilder().withEmployee(ionel).withYear(2014).withMonth(1).withTax(12.0).build();
        ionelFebruary = new MonthlyTaxForEmployeeTestBuilder().withEmployee(ionel).withYear(2014).withMonth(2).withTax(13.0).build();

        List<MonthlyTaxForEmployee> taxes = Arrays.asList(gigelJanuary, gigelFebruary, ionelJanuary, ionelFebruary);
        taxes.forEach(monthlyTaxForEmployeeRepository::save);
    }

    private void saveSuccessFullTaxesFor2014_5(Money... taxes) {
        saveTaxesFor2014_5(false, taxes);
    }

    private void saveFailedTaxesFor2014_5(Money... taxes) {
        saveTaxesFor2014_5(true, taxes);
    }

    private void saveTaxesFor2014_5(boolean hasError, Money... taxes) {
        for (Money tax : taxes) {
            Employee employee = anEmployee();
            employeeRepository.save(employee);

            MonthlyTaxForEmployee monthlyTaxForEmployee = new MonthlyTaxForEmployeeTestBuilder()
                    .withEmployee(employee)
                    .withTax(tax)
                    .withYear(2014)
                    .withMonth(5)
                    .withHasError(hasError)
                    .build();
            monthlyTaxForEmployeeRepository.save(monthlyTaxForEmployee);
        }
    }
}
