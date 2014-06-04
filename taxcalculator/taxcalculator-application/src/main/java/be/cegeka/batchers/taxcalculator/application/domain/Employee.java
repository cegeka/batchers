package be.cegeka.batchers.taxcalculator.application.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;

@NamedQueries({
        @NamedQuery(name = Employee.GET_ALL, query = Employee.GET_ALL_QUERY),
       // @NamedQuery(name = Employee.GET_EMPLOYEES_TOTAL_TAX_NAME, query = Employee.GET_EMPLOYEES_TOTAL_TAX_QUERY),
        @NamedQuery(name = Employee.GET_UNPROCESSED_EMPLOYEES_BY_YEAR_AND_MONTH, query = Employee.GET_UNPROCESSED_EMPLOYEES_BY_YEAR_AND_MONTH_QUERY),
        @NamedQuery(name = Employee.GET_EMPLOYEE_COUNT, query = Employee.GET_EMPLOYEE_COUNT_QUERY)
})

@Entity
public class Employee {
    public static final String GET_ALL = "Employee.getAll";
    public static final String GET_ALL_QUERY = "SELECT e FROM Employee e";

//    public static final String GET_EMPLOYEES_TOTAL_TAX_NAME = "Employee.getWithTotalTax";
//    public static final String GET_EMPLOYEES_TOTAL_TAX_QUERY = "SELECT NEW be.cegeka.batchers.taxcalculator.to.EmployeeTo(e.firstName, e.lastName, e.email, e.income, " +
//            "(select sum(t.tax) from TaxCalculation t where t.employee.id = e.id)) " +
//            "FROM Employee e ORDER BY e.id";

    public static final String GET_UNPROCESSED_EMPLOYEES_BY_YEAR_AND_MONTH = "TaxCalculation.GET_UNPROCESSED_EMPLOYEES_BY_YEAR_AND_MONTH";
    public static final String GET_UNPROCESSED_EMPLOYEES_BY_YEAR_AND_MONTH_QUERY = "SELECT emp FROM Employee emp WHERE NOT EXISTS (SELECT mtfe FROM MonthlyTaxForEmployee mtfe WHERE mtfe.year = :year AND mtfe.month = :month AND mtfe.employee.id = emp.id AND mtfe.lastErrorMessage IS EMPTY)";

    public static final String GET_EMPLOYEE_COUNT = "Employee.getCount";
    public static final String GET_EMPLOYEE_COUNT_QUERY = "SELECT COUNT(e) FROM Employee e";


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

//    @Override
//    public int hashCode() {
//        return HashCodeBuilder.reflectionHashCode(this, "id");
//    }
//
//    @Override
//    public boolean equals(Object that) {
//        return EqualsBuilder.reflectionEquals(this, that, "id");
//    }

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
