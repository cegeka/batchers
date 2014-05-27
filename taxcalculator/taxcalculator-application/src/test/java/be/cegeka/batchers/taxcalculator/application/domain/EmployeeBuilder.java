package be.cegeka.batchers.taxcalculator.application.domain;

public class EmployeeBuilder {
    private Long employeeId;
    private Integer income = 0;
    private String firstName;
    private String lastName;
    private String emailAddress;

    public Employee build() {
        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setIncome(income);
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

    public EmployeeBuilder withId(long employeeId) {
        this.employeeId = employeeId;
        return this;
    }

    public EmployeeBuilder withEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
        return this;
    }
}