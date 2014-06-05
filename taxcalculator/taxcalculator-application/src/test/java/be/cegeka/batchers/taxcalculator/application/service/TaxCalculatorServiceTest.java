package be.cegeka.batchers.taxcalculator.application.service;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.domain.EmployeeTestBuilder;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TaxCalculatorServiceTest {
    @Mock
    RunningTimeService runningTimeService;

    @InjectMocks
    TaxCalculatorService taxCalculatorService;

    private Employee employee;

    @Before
    public void setUp() throws Exception {
        employee = new EmployeeTestBuilder()
                .withIncome(100)
                .withId(1L)
                .build();
    }

    @Test
    public void testCalculateTax() throws Exception {
        Money actualMoney = taxCalculatorService.calculateTax(employee);

        Money expectedMoney = Money.of(CurrencyUnit.EUR, 10);

        assertThat(actualMoney).isEqualTo(expectedMoney);
    }

    @Test
    public void testCalculateTaxSleeps() throws Exception {
        taxCalculatorService.calculateTax(employee);

        verify(runningTimeService).sleep();
    }
}
