package be.cegeka.batchers.taxcalculator.application.service;

import be.cegeka.batchers.taxcalculator.application.domain.*;
import be.cegeka.batchers.taxcalculator.application.repositories.EmployeeRepository;
import be.cegeka.batchers.taxcalculator.application.repositories.MonthlyTaxForEmployeeRepository;
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

    @InjectMocks
    EmployeeService employeeService = new EmployeeService();

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

        verify(employeeRepositoryMock).getEmployeeCount();
        verifyNoMoreInteractions(employeeRepositoryMock);
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
