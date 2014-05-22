package be.cegeka.batchers.taxcalculator.application.domain;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import javax.persistence.*;

@NamedQueries({
        @NamedQuery(name = Employee.GET_ALL_NAME, query = Employee.GET_ALL_QUERY),
        @NamedQuery(name = Employee.GET_EMPLOYEES_TOTAL_TAX_NAME, query = Employee.GET_EMPLOYEES_TOTAL_TAX_QUERY)
})

@Entity
public class Employee {
    public static final String GET_ALL_NAME = "Employee.getAll";
    public static final String GET_ALL_QUERY = "SELECT e FROM Employee e";

    public static final String GET_EMPLOYEES_TOTAL_TAX_NAME = "Employee.getWithTotalTax";
    public static final String GET_EMPLOYEES_TOTAL_TAX_QUERY = "SELECT NEW be.cegeka.batchers.taxcalculator.to.EmployeeTo(e.firstName, e.lastName, e.email, e.income, sum(t.tax)) " +
            "FROM TaxCalculation t RIGHT OUTER JOIN t.employee e GROUP BY e.firstName, e.lastName, e.email, e.income";

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


    public double getIncomeTax() {
        return income * 0.1;
    }

    private boolean taxWasCalculatedThisMonth(DateTime calculationDate) {
        return calculationDate != null && getCurrentMonthInterval().contains(calculationDate);
    }

    private Interval getCurrentMonthInterval() {
        return DateTime.now().monthOfYear().toInterval();
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
        sb.append("income=").append(income);
        sb.append(", firstName='").append(firstName).append('\'');
        sb.append(", lastName='").append(lastName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
