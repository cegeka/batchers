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

@NamedQueries({
        @NamedQuery(name = TaxCalculation.FIND_BY_YEAR_AND_MONTH, query = TaxCalculation.FIND_BY_YEAR_AND_MONTH_QUERY),
        @NamedQuery(name = TaxCalculation.FIND_BY_EMPLOYEE, query = TaxCalculation.FIND_BY_EMPLOYEE_QUERY)
})

@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {TaxCalculation.EMPLOYEE, TaxCalculation.YEAR, TaxCalculation.MONTH})
)

@Entity
public class TaxCalculation {

    public static final String FIND_BY_YEAR_AND_MONTH = "TaxCalculation.FIND_BY_YEAR_AND_MONTH";
    public static final String FIND_BY_YEAR_AND_MONTH_QUERY = "SELECT tc FROM TaxCalculation tc " +
            " WHERE tc.month = :month AND tc.year = :year AND NOT EXISTS (SELECT pc FROM PayCheck pc WHERE" +
                    " pc.taxCalculation.id = tc.id AND NOT (pc.jobExecutionId = :jobExecutionId))";

    public static final String FIND_BY_EMPLOYEE = "TaxCalculation.FIND_BY_EMPLOYEE";
    public static final String FIND_BY_EMPLOYEE_QUERY = "SELECT tc FROM TaxCalculation tc " +
            " WHERE tc.employee.id = :employeeId";

    public static final String EMPLOYEE = "employee_id";
    public static final String MONTH = "month";
    public static final String YEAR = "year";

    @ManyToOne
    @NotNull
    @JoinColumn(name = TaxCalculation.EMPLOYEE)
    private Employee employee;

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private Long jobExecutionId;

    @Min(1)
    @Max(12)
    @NotNull
    @Column(name = TaxCalculation.MONTH)
    private long month;

    @NotNull
    @Column(name = TaxCalculation.YEAR)
    private long year;

    @Type(type = "org.jadira.usertype.moneyandcurrency.joda.PersistentMoneyAmount",
            parameters = {@Parameter(name = "currencyCode", value = "EUR")})
    @NotNull
    private Money tax;

    @JsonSerialize(using = JodaDateTimeSerializer.class)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @NotNull
    private DateTime calculationDate;


    public static TaxCalculation from(Long jobExecutionId, Employee employee, long year, long month, Money tax) {
        TaxCalculation taxCalculation = new TaxCalculation();
        taxCalculation.jobExecutionId = jobExecutionId;
        taxCalculation.employee = employee;
        taxCalculation.year = year;
        taxCalculation.month = month;
        taxCalculation.tax = tax;
        taxCalculation.calculationDate = DateTime.now();
        return taxCalculation;
    }

    public Long getId() {
        return id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public long getMonth() {
        return month;
    }

    public long getYear() {
        return year;
    }

    public Money getTax() {
        return tax;
    }

    public DateTime getCalculationDate() {
        return calculationDate;
    }

    public Long getJobExecutionId() {
        return jobExecutionId;
    }

}
