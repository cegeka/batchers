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
        @NamedQuery(name = MonthlyReport.FIND_BY_YEAR_AND_MONTH, query = MonthlyReport.FIND_BY_YEAR_AND_MONTH_QUERY)
})
@Table(
        uniqueConstraints =
        @UniqueConstraint(columnNames = {MonthlyReport.YEAR, MonthlyReport.MONTH})
)
@Entity
public class MonthlyReport {

    public static final String FIND_BY_YEAR_AND_MONTH = "MonthlyReport.FIND_BY_YEAR_AND_MONTH";
    public static final String FIND_BY_YEAR_AND_MONTH_QUERY = "SELECT mr FROM MonthlyReport mr " +
            " WHERE mr.month = :month AND mr.year = :year";

    public static final String MONTH = "month";
    public static final String YEAR = "year";

    @Id
    @GeneratedValue
    private Long id;

    @Min(1)
    @Max(12)
    @NotNull
    @Column(name = MonthlyReport.MONTH)
    private int month;

    @NotNull
    @Column(name = MonthlyReport.YEAR)
    private int year;

    @Lob
    private byte[] montlyReportPdf;


    @JsonSerialize(using = JodaDateTimeSerializer.class)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @NotNull
    private DateTime calculationDate;

    public static MonthlyReport from(int year, int month, byte[] montlyReportPdf, DateTime calculationDate) {
        MonthlyReport monthlyReport = new MonthlyReport();
        monthlyReport.year = year;
        monthlyReport.month = month;
        monthlyReport.montlyReportPdf = montlyReportPdf;
        monthlyReport.calculationDate = calculationDate;
        return monthlyReport;
    }

    public Long getId() {
        return id;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public byte[] getMontlyReportPdf() {
        return montlyReportPdf;
    }

    public DateTime getCalculationDate() {
        return calculationDate;
    }
}
