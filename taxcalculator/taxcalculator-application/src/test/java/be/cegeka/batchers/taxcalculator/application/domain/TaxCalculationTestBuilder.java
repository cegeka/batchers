package be.cegeka.batchers.taxcalculator.application.domain;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

public class TaxCalculationTestBuilder {

    private long jobExecutionId = 1L;
    private Employee employee;
    private int year = 2014;
    private int month = 1;
    private Money tax = Money.of(CurrencyUnit.EUR, 2000.0);

    public TaxCalculation build() {
        return TaxCalculation.from(jobExecutionId, employee, year, month, tax);
    }

    public TaxCalculationTestBuilder withJobExecutionId(long jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
        return this;
    }

    public TaxCalculationTestBuilder withEmployee(Employee employee) {
        this.employee = employee;
        return this;
    }

    public TaxCalculationTestBuilder withYear(int year) {
        this.year = year;
        return this;
    }

    public TaxCalculationTestBuilder withMonth(int month) {
        this.month = month;
        return this;
    }

    public TaxCalculationTestBuilder withTax(Money tax) {
        this.tax = tax;
        return this;
    }

    public TaxCalculationTestBuilder withTax(double tax) {
        this.tax = Money.of(CurrencyUnit.EUR, tax);
        return this;
    }
}
