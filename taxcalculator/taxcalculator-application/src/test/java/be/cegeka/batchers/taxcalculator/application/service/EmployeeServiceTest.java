package be.cegeka.batchers.taxcalculator.application.service;

import be.cegeka.batchers.taxcalculator.application.domain.*;
import be.cegeka.batchers.taxcalculator.application.domain.EmployeeRepository;
import be.cegeka.batchers.taxcalculator.application.domain.MonthlyTaxForEmployeeRepository;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class EmployeeServiceTest {
    public static final long EMPLOYEE_ID = 123L;

    @InjectMocks
    private EmployeeService employeeService = new EmployeeService();

    @Mock
    private EmployeeRepository employeeRepositoryMock;
    @Mock
    private MonthlyTaxForEmployeeRepository monthlyTaxForEmployeeRepository;

    @Test
    public void givenEmployees_whenGetEmployees_thenRepositoryIsCalledWithCorrectParameters() throws Exception {
        employeeService.getEmployees(0, 10);

        verify(employeeRepositoryMock).getEmployees(0, 10);
        verifyNoMoreInteractions(employeeRepositoryMock);
    }

    @Test
    public void givenEmployees_whenGetEmployeeCount_thenRepositoryIsCalled() throws Exception {
        employeeService.getEmployeeCount();

        verify(employeeRepositoryMock).count();
        verifyNoMoreInteractions(employeeRepositoryMock);
    }

    @Test
    public void testGetEmployee() throws Exception {
        employeeService.getEmployee(EMPLOYEE_ID);
        verify(employeeRepositoryMock).getBy(EMPLOYEE_ID);
        verifyNoMoreInteractions(employeeRepositoryMock);
    }

    @Test
    public void testGetEmployeeTaxes() throws Exception {
        Employee employee = new Employee();
        when(employeeService.getEmployee(EMPLOYEE_ID)).thenReturn(employee);

        employeeService.getEmployeeTaxes(EMPLOYEE_ID);

        verify(employeeRepositoryMock).getBy(EMPLOYEE_ID);
        verify(monthlyTaxForEmployeeRepository).findByEmployee(employee);
    }

    @Test
    public void getTotalAmountOfPaidTaxes() throws Exception {
        Employee employee = new EmployeeTestBuilder().build();
        when(monthlyTaxForEmployeeRepository.findByEmployee(employee)).thenReturn(
                Arrays.asList(
                        new MonthlyTaxForEmployeeTestBuilder().withHasError(false).withTax(10.0).build(),
                        new MonthlyTaxForEmployeeTestBuilder().withHasError(false).withTax(10.0).build(),
                        new MonthlyTaxForEmployeeTestBuilder().withHasError(true).withTax(10.0).build()
                )
        );

        Money totalAmountOfPaidTaxes = employeeService.getTotalAmountOfPaidTaxes(employee);

        assertThat(totalAmountOfPaidTaxes).isEqualTo(Money.of(CurrencyUnit.EUR, 20.0));
    }

}
