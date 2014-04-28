package be.cegeka.batchers.springbatch.domain;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Employee {
    private Integer income;

    @Id
    @GeneratedValue
    private Long id;
    private String firstName;
    private String lastName;
    private String address;
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime calculationDate;
    private int taxTotal;

    public void setIncome(int income) {
        this.income = income;
    }

    public Integer getIncome() {
        return income;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public DateTime getCalculationDate() {
        return calculationDate;
    }


    public void setTaxTotal(int taxTotal) {
        this.taxTotal = taxTotal;
        this.calculationDate = new DateTime();
    }

    public int getTaxTotal() {
        return taxTotal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;

        Employee employee = (Employee) o;

        if (taxTotal != employee.taxTotal) return false;
        if (address != null ? !address.equals(employee.address) : employee.address != null) return false;
        if (calculationDate != null ? !calculationDate.equals(employee.calculationDate) : employee.calculationDate != null)
            return false;
        if (firstName != null ? !firstName.equals(employee.firstName) : employee.firstName != null) return false;
        if (id != null ? !id.equals(employee.id) : employee.id != null) return false;
        if (income != null ? !income.equals(employee.income) : employee.income != null) return false;
        if (lastName != null ? !lastName.equals(employee.lastName) : employee.lastName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = income != null ? income.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (calculationDate != null ? calculationDate.hashCode() : 0);
        result = 31 * result + taxTotal;
        return result;
    }
}
