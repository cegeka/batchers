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
        @NamedQuery(name = MonthlyReport.FIND_BY_ID, query = MonthlyReport.FIND_BY_ID_QUERY),
        @NamedQuery(name = MonthlyReport.GET_SUCCESS_SUM, query = MonthlyReport.GET_SUCCESS_SUM_QUERY),
        @NamedQuery(name = MonthlyReport.GET_FAILED_SUM, query = MonthlyReport.GET_FAILED_SUM_QUERY),

})
@Entity
public class MonthlyReport {

    public static final String FIND_BY_YEAR_AND_MONTH = "MonthlyReport.FIND_BY_YEAR_AND_MONTH";
    public static final String FIND_BY_YEAR_AND_MONTH_QUERY = "SELECT mr FROM MonthlyReport mr " +
            " WHERE  mr.year = :year AND mr.month = :month ORDER BY mr.year DESC, mr.month DESC";

    public static final String FIND_BY_ID = "MonthlyReport.FIND_BY_ID";
    public static final String FIND_BY_ID_QUERY = "SELECT mr FROM MonthlyReport mr " +
            " WHERE mr.id = :id";

    public static final String GET_SUCCESS_SUM = "MonthlyReport.GET_SUCCESS_SUM";
    public static final String GET_SUCCESS_SUM_QUERY = "SELECT SUM(tc.tax) FROM TaxCalculation tc" +
            " WHERE tc.month = :month and tc.year = :year " +
            " AND EXISTS (SELECT pc FROM PayCheck pc WHERE pc.taxCalculation.id = tc.id)";

    public static final String GET_FAILED_SUM = "MonthlyReport.GET_FAILED_SUM";
    public static final String GET_FAILED_SUM_QUERY = "SELECT SUM(tc.tax) FROM TaxCalculation tc" +
            " WHERE tc.month = :month and tc.year = :year " +
            " AND NOT EXISTS (SELECT pc FROM PayCheck pc WHERE pc.taxCalculation.id = tc.id)";

    public static final String ID = "id";
    public static final String YEAR = "year";
    public static final String MONTH = "month";

    @Id
    @Column(name = MonthlyReport.ID)
    private Long id;

    @NotNull
    @Column(name = MonthlyReport.YEAR)
    private int year;

    @Min(1)
    @Max(12)
    @NotNull
    @Column(name = MonthlyReport.MONTH)
    private int month;

    @Lob
    private byte[] montlyReportPdf;

    @JsonSerialize(using = JodaDateTimeSerializer.class)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @NotNull
    private DateTime calculationDate;

    public static MonthlyReport from(Long id, int year, int month, byte[] montlyReportPdf, DateTime calculationDate) {
        MonthlyReport monthlyReport = new MonthlyReport();
        monthlyReport.id = id;
        monthlyReport.year = year;
        monthlyReport.month = month;
        monthlyReport.montlyReportPdf = montlyReportPdf;
        monthlyReport.calculationDate = calculationDate;
        return monthlyReport;
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

    public byte[] getMonthlyReportPdf() {
        return montlyReportPdf;
    }

    public DateTime getCalculationDate() {
        return calculationDate;
    }

}
