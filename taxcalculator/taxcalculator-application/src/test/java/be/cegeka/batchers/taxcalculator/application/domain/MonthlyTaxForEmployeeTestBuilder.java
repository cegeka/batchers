package be.cegeka.batchers.taxcalculator.application.domain;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import static be.cegeka.batchers.taxcalculator.application.domain.MonthlyTaxForEmployee.from;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class MonthlyTaxForEmployeeTestBuilder {

    private Employee employee = new EmployeeTestBuilder().build();
    private int year = 2014;
    private int month = 6;
    private Money tax = Money.of(CurrencyUnit.EUR, 100);
    private String lastErrorMessage;
    private byte[] pdfByteArray = new byte[20];

    public MonthlyTaxForEmployee build() {
        if (isNotBlank(lastErrorMessage)) {
            return from(employee, year, month, tax, lastErrorMessage);
        } else {
            return from(employee, year, month, tax, pdfByteArray);
        }
    }

    public MonthlyTaxForEmployeeTestBuilder withEmployee(Employee employee) {
        this.employee = employee;
        return this;
    }

    public MonthlyTaxForEmployeeTestBuilder withYear(int year) {
        this.year = year;
        return this;
    }

    public MonthlyTaxForEmployeeTestBuilder withMonth(int month) {
        this.month = month;
        return this;
    }

    public MonthlyTaxForEmployeeTestBuilder withTax(Money tax) {
        this.tax = tax;
        return this;
    }

    public MonthlyTaxForEmployeeTestBuilder withTax(double tax) {
        this.tax = Money.of(CurrencyUnit.EUR, tax);
        return this;
    }

    public MonthlyTaxForEmployeeTestBuilder withLastErrorMessage(String lastErrorMessage) {
        this.lastErrorMessage = lastErrorMessage;
        return this;
    }

    public MonthlyTaxForEmployeeTestBuilder withAPdf() {
        this.lastErrorMessage = null;
        this.pdfByteArray = new byte[10];
        return this;
    }

    public MonthlyTaxForEmployeeTestBuilder withPdfByteArray(byte[] pdfByteArray) {
        this.pdfByteArray = pdfByteArray;
        return this;
    }

    public MonthlyTaxForEmployeeTestBuilder withHasError(boolean yesOrNo) {
        if(yesOrNo) {
            this.lastErrorMessage = "an error occurred";
            this.pdfByteArray = null;
        } else {
            this.lastErrorMessage = null;
            this.pdfByteArray = new byte[10];
        }
        return this;
    }
}
