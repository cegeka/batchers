package be.cegeka.batchers.taxcalculator.application.domain;

import be.cegeka.batchers.taxcalculator.application.infrastructure.IntegrationTest;
import org.fest.assertions.api.Assertions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

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

    @Before
    public void setup() {
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
        //ACT
        List<MonthlyTaxForEmployee> monthlyTaxForEmployees = monthlyTaxForEmployeeRepository.findByEmployee(ionel);

        //ASSERT
        assertThat(monthlyTaxForEmployees).containsOnly(ionelJanuary, ionelFebruary);
    }

    @Test
    public void testFind() {
        //ACT
        MonthlyTaxForEmployee monthlyTaxForEmployee = monthlyTaxForEmployeeRepository.find(ionel, 2014, 1);

        //ASSERT
        assertThat(monthlyTaxForEmployee).isEqualsToByComparingFields(ionelJanuary);
    }
}
