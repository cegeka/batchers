package be.cegeka.batchers.taxcalculator.application.domain;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class MonthlyTaxForEmployeeTest {

    @Test
    public void dateTimeIsFilledIn() {
        MonthlyTaxForEmployee monthlyTaxForEmployee = MonthlyTaxForEmployee.from(new EmployeeTestBuilder().build(), 2014, 5, Money.of(CurrencyUnit.EUR, 10.00), "there was an error");
        assertThat(monthlyTaxForEmployee.getCalculationDate()).isNotNull();
    }

    @Test
    public void hasErrorMessage_True() {
        MonthlyTaxForEmployee monthlyTaxForEmployee = MonthlyTaxForEmployee.from(new EmployeeTestBuilder().build(), 2014, 5, Money.of(CurrencyUnit.EUR, 10.00), "there was an error");
        assertThat(monthlyTaxForEmployee.hasErrorMessage()).isTrue();
    }

    @Test
    public void hasErrorMessage_False() {
        MonthlyTaxForEmployee monthlyTaxForEmployee = MonthlyTaxForEmployee.from(new EmployeeTestBuilder().build(), 2014, 5, Money.of(CurrencyUnit.EUR, 10.00), new byte[10]);
        assertThat(monthlyTaxForEmployee.hasErrorMessage()).isFalse();
    }
}
