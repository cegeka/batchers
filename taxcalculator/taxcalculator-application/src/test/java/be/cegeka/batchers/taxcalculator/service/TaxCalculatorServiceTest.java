package be.cegeka.batchers.taxcalculator.service;

import be.cegeka.batchers.taxcalculator.domain.Employee;
import be.cegeka.batchers.taxcalculator.domain.EmployeeBuilder;
import be.cegeka.batchers.taxcalculator.domain.EmployeeRepository;
import org.fest.assertions.api.Assertions;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TaxCalculatorServiceTest {
    @Mock
    RunningTimeService runningTimeService;

    @Mock
    EmployeeRepository employeeRepositoryMock;

    @InjectMocks
    TaxCalculatorService taxCalculatorService;

    private Employee employee;

    @Before
    public void setUp() throws Exception {
        employee = new EmployeeBuilder().build();
        employee.setIncome(100);
    }

    @Test
    public void testCalculateTax() throws Exception {
        taxCalculatorService.calculateTax(employee);

        ArgumentCaptor<Employee> argumentCaptor = ArgumentCaptor.forClass(Employee.class);

        verify(employeeRepositoryMock).save(argumentCaptor.capture());
        Employee capturedEmployee = argumentCaptor.getValue();
        Money expectedMoney = Money.of(CurrencyUnit.EUR, 10);
        Assertions.assertThat(capturedEmployee.getTaxTotal()).isEqualTo(expectedMoney);
    }

    @Test
    public void testCalculateTaxSleeps() throws Exception {
        taxCalculatorService.calculateTax(employee);

        verify(runningTimeService).sleep();
    }
}
