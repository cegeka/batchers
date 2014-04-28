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
}
