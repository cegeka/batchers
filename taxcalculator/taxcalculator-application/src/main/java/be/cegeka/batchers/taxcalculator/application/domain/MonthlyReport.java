package be.cegeka.batchers.taxcalculator.application.domain;

import be.cegeka.batchers.taxcalculator.application.util.jackson.JodaDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@NamedQueries({
        @NamedQuery(name = MonthlyReport.FIND_BY_YEAR_AND_MONTH, query = MonthlyReport.FIND_BY_YEAR_AND_MONTH_QUERY),
        @NamedQuery(name = MonthlyReport.FIND_BY_JOBEXECUTIONID, query = MonthlyReport.FIND_BY_JOBEXECUTIONID_QUERY)
})
@Entity
public class MonthlyReport {

    public static final String FIND_BY_YEAR_AND_MONTH = "MonthlyReport.FIND_BY_YEAR_AND_MONTH";
    public static final String FIND_BY_JOBEXECUTIONID_QUERY = "SELECT mr FROM MonthlyReport mr " +
            " WHERE mr.jobExecutionId = :jobExecutionId";

    public static final String FIND_BY_JOBEXECUTIONID = "MonthlyReport.FIND_BY_JOBEXECUTIONID";
    public static final String FIND_BY_YEAR_AND_MONTH_QUERY = "SELECT mr FROM MonthlyReport mr " +
            " WHERE mr.month = :month AND mr.year = :year";


    public static final String MONTH = "month";
    public static final String YEAR = "year";
    public static final String JOBEXECUTIONID = "jobExecutionId";

    @Id
    @GeneratedValue
    private Long id;

    @Min(1)
    @Max(12)
    @NotNull
    @Column(name = MonthlyReport.MONTH)
    private long month;

    @NotNull
    @Column(name = MonthlyReport.YEAR)
    private long year;

    @Lob
    private byte[] montlyReportPdf;


    @JsonSerialize(using = JodaDateTimeSerializer.class)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @NotNull
    private DateTime calculationDate;

    @Column(unique = true)
    private Long jobExecutionId;

    public static MonthlyReport from(long year, long month, byte[] montlyReportPdf, DateTime calculationDate, Long jobExecutionId) {
        MonthlyReport monthlyReport = new MonthlyReport();
        monthlyReport.year = year;
        monthlyReport.month = month;
        monthlyReport.montlyReportPdf = montlyReportPdf;
        monthlyReport.calculationDate = calculationDate;
        monthlyReport.jobExecutionId = jobExecutionId;
        return monthlyReport;
    }

    public static MonthlyReport from(long year, long month, byte[] montlyReportPdf, DateTime calculationDate) {
        return from(year, month, montlyReportPdf, calculationDate, null);
    }

    public Long getId() {
        return id;
    }

    public long getMonth() {
        return month;
    }

    public long getYear() {
        return year;
    }

    public byte[] getMontlyReportPdf() {
        return montlyReportPdf;
    }

    public DateTime getCalculationDate() {
        return calculationDate;
    }

    public void setJobExecutionId(Long jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }

    public Long getJobExecutionId() {
        return jobExecutionId;
    }
}
