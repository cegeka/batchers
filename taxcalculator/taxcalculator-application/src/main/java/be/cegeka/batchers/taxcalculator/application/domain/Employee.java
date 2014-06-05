package be.cegeka.batchers.taxcalculator.application.domain;

import javax.persistence.*;

@NamedQueries({
        @NamedQuery(name = Employee.GET_ALL, query = Employee.GET_ALL_QUERY)
})

@Entity
public class Employee {
    public static final String GET_ALL = "Employee.getAll";
    public static final String GET_ALL_QUERY = "SELECT e FROM Employee e";

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
