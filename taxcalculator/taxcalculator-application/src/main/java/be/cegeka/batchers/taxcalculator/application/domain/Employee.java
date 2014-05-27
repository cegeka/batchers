package be.cegeka.batchers.taxcalculator.application.domain;

import javax.persistence.*;

@NamedQueries({
        @NamedQuery(name = Employee.GET_ALL_NAME, query = Employee.GET_ALL_QUERY),
        @NamedQuery(name = Employee.GET_EMPLOYEES_TOTAL_TAX_NAME, query = Employee.GET_EMPLOYEES_TOTAL_TAX_QUERY),
        @NamedQuery(name = Employee.GET_UNPROCESSED_EMPLOYEES_BY_YEAR_AND_MONTH, query = Employee.GET_UNPROCESSED_EMPLOYEES_BY_YEAR_AND_MONTH_QUERY)
})

@Entity
public class Employee {
    public static final String GET_ALL_NAME = "Employee.getAll";
    public static final String GET_ALL_QUERY = "SELECT e FROM Employee e";

    public static final String GET_EMPLOYEES_TOTAL_TAX_NAME = "Employee.getWithTotalTax";
    public static final String GET_EMPLOYEES_TOTAL_TAX_QUERY = "SELECT NEW be.cegeka.batchers.taxcalculator.to.EmployeeTo(e.firstName, e.lastName, e.email, e.income, sum(t.tax)) " +
            "FROM TaxCalculation t RIGHT OUTER JOIN t.employee e GROUP BY e ORDER BY e.id";

    public static final String GET_UNPROCESSED_EMPLOYEES_BY_YEAR_AND_MONTH = "TaxCalculation.GET_UNPROCESSED_EMPLOYEES_BY_YEAR_AND_MONTH";
    public static final String GET_UNPROCESSED_EMPLOYEES_BY_YEAR_AND_MONTH_QUERY = "SELECT emp FROM Employee emp WHERE NOT EXISTS (SELECT tc FROM TaxCalculation tc WHERE tc.month = :month AND tc.year = :year AND tc.employee.id = emp.id AND NOT (tc.jobExecutionId = :jobExecutionId))";


    private Integer income;

    @Id
    @GeneratedValue
    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    public Integer getIncome() {
        return income;
    }

    public void setIncome(int income) {
        this.income = income;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String fullName() {
        return getFirstName() + " " + getLastName();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Employee{");
        sb.append("id=").append(id);
        sb.append(", income='").append(income).append('\'');
        sb.append(", firstName='").append(firstName).append('\'');
        sb.append(", lastName='").append(lastName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
