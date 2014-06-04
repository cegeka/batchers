package be.cegeka.batchers.taxcalculator.batch.domain;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.util.jackson.JodaDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
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
        @NamedQuery(name = TaxCalculation.FIND_BY_EMPLOYEE, query = TaxCalculation.FIND_BY_EMPLOYEE_QUERY),
        @NamedQuery(name = TaxCalculation.GET_SUCCESS_SUM, query = TaxCalculation.GET_SUCCESS_SUM_QUERY),
        @NamedQuery(name = TaxCalculation.GET_FAILED_SUM, query = TaxCalculation.GET_FAILED_SUM_QUERY),
        @NamedQuery(name = TaxCalculation.GET_UNPROCESSED_EMPLOYEES_BY_YEAR_AND_MONTH, query = TaxCalculation.GET_UNPROCESSED_EMPLOYEES_BY_YEAR_AND_MONTH_QUERY),
        @NamedQuery(name = TaxCalculation.GET_UNPROCESSED_EMPLOYEES_BY_YEAR_AND_MONTH_SLAVE, query = TaxCalculation.GET_UNPROCESSED_EMPLOYEES_BY_YEAR_AND_MONTH_QUERY_SLAVE),
        @NamedQuery(name = TaxCalculation.GET_UNPROCESSED_EMPLOYEES_IDS_BY_YEAR_AND_MONTH, query = TaxCalculation.GET_UNPROCESSED_EMPLOYEES_IDS_BY_YEAR_AND_MONTH_QUERY)
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

    public static final String GET_SUCCESS_SUM = "TaxCalculation.GET_SUCCESS_SUM";
    public static final String GET_SUCCESS_SUM_QUERY = "SELECT SUM(tc.tax) FROM TaxCalculation tc" +
            " WHERE tc.month = :month and tc.year = :year " +
            " AND EXISTS (SELECT pc FROM PayCheck pc WHERE pc.taxCalculation.id = tc.id)";

    public static final String GET_FAILED_SUM = "TaxCalculation.GET_FAILED_SUM";
    public static final String GET_FAILED_SUM_QUERY = "SELECT SUM(tc.tax) FROM TaxCalculation tc" +
            " WHERE tc.month = :month and tc.year = :year " +
            " AND NOT EXISTS (SELECT pc FROM PayCheck pc WHERE pc.taxCalculation.id = tc.id)";

    public static final String GET_UNPROCESSED_EMPLOYEES_BY_YEAR_AND_MONTH = "TaxCalculation.GET_UNPROCESSED_EMPLOYEES_BY_YEAR_AND_MONTH";
    public static final String GET_UNPROCESSED_EMPLOYEES_BY_YEAR_AND_MONTH_QUERY = "SELECT emp FROM Employee emp WHERE NOT EXISTS (SELECT tc FROM TaxCalculation tc WHERE tc.month = :month AND tc.year = :year AND tc.employee.id = emp.id AND NOT (tc.jobExecutionId = :jobExecutionId))";

    public static final String GET_UNPROCESSED_EMPLOYEES_BY_YEAR_AND_MONTH_SLAVE = "TaxCalculation.GET_UNPROCESSED_EMPLOYEES_BY_YEAR_AND_MONTH_SLAVE";
    public static final String GET_UNPROCESSED_EMPLOYEES_BY_YEAR_AND_MONTH_QUERY_SLAVE = "SELECT emp FROM Employee emp WHERE NOT EXISTS (SELECT tc FROM TaxCalculation tc WHERE tc.month = :month AND tc.year = :year AND tc.employee.id = emp.id AND NOT (tc.jobExecutionId = :jobExecutionId)) AND emp.id >= :minId AND emp.id <= :maxId";

    public static final String GET_UNPROCESSED_EMPLOYEES_IDS_BY_YEAR_AND_MONTH = "TaxCalculation.GET_UNPROCESSED_EMPLOYEES_IDS_BY_YEAR_AND_MONTH";
    public static final String GET_UNPROCESSED_EMPLOYEES_IDS_BY_YEAR_AND_MONTH_QUERY = "SELECT emp.id FROM Employee emp WHERE NOT EXISTS (SELECT tc FROM TaxCalculation tc WHERE tc.month = :month AND tc.year = :year AND tc.employee.id = emp.id AND NOT (tc.jobExecutionId = :jobExecutionId)) ORDER BY emp.id";

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
    private int month;

    @NotNull
    @Column(name = TaxCalculation.YEAR)
    private int year;

    @Type(type = "org.jadira.usertype.moneyandcurrency.joda.PersistentMoneyAmount",
            parameters = {@Parameter(name = "currencyCode", value = "EUR")})
    @NotNull
    private Money tax;

    @JsonSerialize(using = JodaDateTimeSerializer.class)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @NotNull
    private DateTime calculationDate;


    public static TaxCalculation from(Long jobExecutionId, Employee employee, int year, int month, Money tax) {
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

    public int getMonth() {
        return month;
    }

    public int getYear() {
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

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, "id");
    }

    @Override
    public boolean equals(Object that) {
        return EqualsBuilder.reflectionEquals(this, that, "id");
    }
}
