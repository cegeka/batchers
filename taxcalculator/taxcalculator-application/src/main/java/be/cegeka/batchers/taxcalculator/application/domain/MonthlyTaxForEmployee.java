package be.cegeka.batchers.taxcalculator.application.domain;

import be.cegeka.batchers.taxcalculator.application.util.jackson.JodaDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.joda.money.Money;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@NamedQueries({
        @NamedQuery(name = MonthlyTaxForEmployee.FIND_BY_EMPLOYEE, query = MonthlyTaxForEmployee.FIND_BY_EMPLOYEE_QUERY),
        @NamedQuery(name = MonthlyTaxForEmployee.FIND_BY_EMPLOYEE_YEAR_AND_MONTH, query = MonthlyTaxForEmployee.FIND_BY_EMPLOYEE_YEAR_AND_MONTH_QUERY)
})

@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {MonthlyTaxForEmployee.EMPLOYEE, MonthlyTaxForEmployee.YEAR, MonthlyTaxForEmployee.MONTH})
)

@Entity
public class MonthlyTaxForEmployee {

    public static final String EMPLOYEE = "employee_id";
    public static final String MONTH = "month";
    public static final String YEAR = "year";

    public static final String FIND_BY_EMPLOYEE = "MonthlyTaxForEmployee.FIND_BY_EMPLOYEE";
    public static final String FIND_BY_EMPLOYEE_QUERY = "SELECT mtfe FROM MonthlyTaxForEmployee mtfe WHERE mtfe.employee.id = :employeeId";
    public static final String FIND_BY_EMPLOYEE_YEAR_AND_MONTH = "MonthlyTaxForEmployee.FIND_BY_EMPLOYEE_YEAR_AND_MONTH";
    public static final String FIND_BY_EMPLOYEE_YEAR_AND_MONTH_QUERY = "SELECT mtfe FROM MonthlyTaxForEmployee mtfe WHERE mtfe.employee.id = :employeeId and mtfe.year = :year and mtfe.month = :month";

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = MonthlyTaxForEmployee.EMPLOYEE)
    private Employee employee;

    @NotNull
    @Column(name = MonthlyTaxForEmployee.YEAR)
    private int year;

    @Min(1)
    @Max(12)
    @NotNull
    @Column(name = MonthlyTaxForEmployee.MONTH)
    private int month;

    @Type(type = "org.jadira.usertype.moneyandcurrency.joda.PersistentMoneyAmount",
            parameters = {@Parameter(name = "currencyCode", value = "EUR")})
    @NotNull
    private Money tax;

    @JsonSerialize(using = JodaDateTimeSerializer.class)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @NotNull
    private DateTime calculationDate;

    @Lob
    @Column(length = 10 * 1024 * 1024)
    private byte[] monthlyReportPdf;

    private String lastErrorMessage;

    public static MonthlyTaxForEmployee from(Employee employee, int year, int month, Money tax, byte[] monthlyReportPdf) {
        return new MonthlyTaxForEmployee(employee, year, month, tax, monthlyReportPdf, null);
    }

    public static MonthlyTaxForEmployee from(Employee employee, int year, int month, Money tax, String lastErrorMessage) {
        return new MonthlyTaxForEmployee(employee, year, month, tax, null, lastErrorMessage);
    }

    private MonthlyTaxForEmployee() {
        //needed for JPA
    }

    private MonthlyTaxForEmployee(Employee employee, int year, int month, Money tax, byte[] monthlyReportPdf, String lastErrorMessage) {
        this.employee = employee;
        this.year = year;
        this.month = month;
        this.tax = tax;
        this.monthlyReportPdf = monthlyReportPdf;
        this.lastErrorMessage = lastErrorMessage;
        this.calculationDate = DateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public Money getTax() {
        return tax;
    }

    public DateTime getCalculationDate() {
        return calculationDate;
    }

    public byte[] getMonthlyReportPdf() {
        return monthlyReportPdf;
    }

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    public boolean hasErrorMessage() {
        return isNotBlank(lastErrorMessage);
    }

    public void setCalculationDate(DateTime calculationDate) {
        this.calculationDate = calculationDate;
    }

    public void setMonthlyReportPdf(byte[] monthlyReportPdf) {
        this.monthlyReportPdf = monthlyReportPdf;
    }

    public void setLastErrorMessage(String lastErrorMessage) {
        this.lastErrorMessage = lastErrorMessage;
    }
}
