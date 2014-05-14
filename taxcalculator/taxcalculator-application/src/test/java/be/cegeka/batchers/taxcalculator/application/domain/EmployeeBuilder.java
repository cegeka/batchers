package be.cegeka.batchers.taxcalculator.application.domain;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import org.joda.money.Money;
import org.joda.time.DateTime;

public class EmployeeBuilder {
    private Long employeeId;
    private Integer income = 0;
    private String firstName;
    private String lastName;
    private DateTime calculationDate;
    private Money taxTotal;
    private String emailAddress;

    public Employee build() {
        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setIncome(income);
        employee.setCalculationDate(calculationDate);
        employee.setEmail(emailAddress);
        return employee;
    }

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



    public EmployeeBuilder withId(long employeeId) {
        this.employeeId = employeeId;
        return this;
    }

    public EmployeeBuilder withEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
        return this;
    }
}