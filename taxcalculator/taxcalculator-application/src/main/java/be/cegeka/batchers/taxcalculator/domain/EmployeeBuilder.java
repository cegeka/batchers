package be.cegeka.batchers.taxcalculator.domain;

import org.joda.money.Money;
import org.joda.time.DateTime;

public class EmployeeBuilder {
    private Integer income;
    private String firstName;
    private String lastName;
    private DateTime calculationDate;
    private Money taxTotal;

    public EmployeeBuilder withIncome(Integer income) {
        this.income = income;
        return this;
    }

    public EmployeeBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public EmployeeBuilder withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public EmployeeBuilder withCalculationDate(DateTime calculationDate) {
        this.calculationDate = calculationDate;
        return this;
    }

    public EmployeeBuilder withTaxTotal(Money taxTotal) {
        this.taxTotal = taxTotal;
        return this;
    }

    public Employee createEmployee() {
        return new Employee(income, firstName, lastName, calculationDate, taxTotal);
    }
}