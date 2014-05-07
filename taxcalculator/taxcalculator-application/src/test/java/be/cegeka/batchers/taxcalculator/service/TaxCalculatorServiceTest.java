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
        employee = new EmployeeBuilder()
                .withIncome(100)
                .build();
    }

    @Test
    public void testCalculateTax() throws Exception {
        taxCalculatorService.calculateTax(employee);

        Money expectedMoney = Money.of(CurrencyUnit.EUR, 10);
        assertThat(employee.getTaxTotal()).isEqualTo(expectedMoney);
    }

    @Test
    public void testCalculateTaxSleeps() throws Exception {
        taxCalculatorService.calculateTax(employee);

        verify(runningTimeService).sleep();
    }
}
