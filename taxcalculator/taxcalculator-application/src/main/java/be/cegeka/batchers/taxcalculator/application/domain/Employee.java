package be.cegeka.batchers.taxcalculator.application.domain;

import be.cegeka.batchers.taxcalculator.application.util.jackson.JodaDateTimeSerializer;
import be.cegeka.batchers.taxcalculator.application.util.jackson.JodaMoneySerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import javax.persistence.*;
import java.math.RoundingMode;

@NamedQueries({
        @NamedQuery(name = Employee.GET_ALL_NAME, query = Employee.GET_ALL_QUERY),
        @NamedQuery(name = Employee.GET_EMPLOYEES_TOTAL_TAX_NAME, query = Employee.GET_EMPLOYEES_TOTAL_TAX_QUERY)
})

@Entity
public class Employee {
    public static final String GET_ALL_NAME = "Employee.getAll";
    public static final String GET_ALL_QUERY = "SELECT e FROM Employee e";

    public static final String GET_EMPLOYEES_TOTAL_TAX_NAME = "Employee.getWithTax";
    public static final String GET_EMPLOYEES_TOTAL_TAX_QUERY = "SELECT NEW be.cegeka.batchers.taxcalculator.to.EmployeeTo(t.employee.firstName, t.employee.lastName, t.employee.email, t.employee.income, sum(t.tax)) " +
            "FROM TaxCalculation t GROUP BY t.employee.firstName, t.employee.lastName, t.employee.email, t.employee.income";

    private Integer income;

    @Id
    @GeneratedValue
    private Long id;
    private String firstName;
    private String lastName;
    @JsonSerialize(using = JodaDateTimeSerializer.class)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime calculationDate;
    @JsonSerialize(using = JodaMoneySerializer.class)
    @Type(type = "org.jadira.usertype.moneyandcurrency.joda.PersistentMoneyAmount",
            parameters = {@Parameter(name = "currencyCode", value = "EUR")})
    private Money taxTotal = Money.zero(CurrencyUnit.EUR);
    private String email;

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

    public String fullName() {
        return getFirstName() + " " + getLastName();
    }

    public String getLastName() {
        return lastName;
    }

    public DateTime getCalculationDate() {
        return calculationDate;
    }

    public void addTax() {
        if (!taxWasCalculatedThisMonth(calculationDate)) {
            double amount = getIncomeTax();
            CurrencyUnit currency = taxTotal.getCurrencyUnit();
            this.taxTotal = Money.total(taxTotal, Money.of(currency, amount, RoundingMode.HALF_DOWN));
            this.calculationDate = new DateTime();
        }
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

    /**
     * used only in testing
     *
     * @param calculationDate the date when it was calculated
     */
    public void setCalculationDate(DateTime calculationDate) {
        this.calculationDate = calculationDate;
    }

    public Money getTaxTotal() {
        return taxTotal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Employee)) {
            return false;
        }

        Employee employee = (Employee) o;

        if (isNotEqual(calculationDate, employee.calculationDate) ||
                isNotEqual(firstName, employee.firstName) ||
                isNotEqual(id, employee.id) ||
                isNotEqual(income, employee.income) ||
                isNotEqual(lastName, employee.lastName) ||
                isNotEqual(taxTotal, employee.taxTotal)) {
            return false;
        }
        return true;
    }

    private boolean isNotEqual(Object self, Object other) {
        if (self != null) {
            return !self.equals(other);
        } else {
            return other != null;
        }
    }

    @Override
    public int hashCode() {
        int result = income != null ? income.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (calculationDate != null ? calculationDate.hashCode() : 0);
        result = 31 * result + (taxTotal != null ? taxTotal.hashCode() : 0);
        return result;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Employee{");
        sb.append("income=").append(income);
        sb.append(", firstName='").append(firstName).append('\'');
        sb.append(", lastName='").append(lastName).append('\'');
        sb.append(", calculationDate=").append(calculationDate);
        sb.append(", taxTotal=").append(taxTotal);
        sb.append('}');
        return sb.toString();
    }
}
