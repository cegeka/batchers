package be.cegeka.batchers.taxcalculator.application.domain;

import be.cegeka.batchers.taxcalculator.application.util.jackson.JodaDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.http.HttpStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@NamedQueries({
        @NamedQuery(name = TaxServiceCallResult.FIND_BY_TAXCALCULATION, query = TaxServiceCallResult.FIND_BY_TAXCALCULATION_QUERY),
        @NamedQuery(name = TaxServiceCallResult.GET_SUCCESS_SUM, query = TaxServiceCallResult.GET_SUCCESS_SUM_QUERY),
        @NamedQuery(name = TaxServiceCallResult.GET_FAILED_SUM, query = TaxServiceCallResult.GET_FAILED_SUM_QUERY),
        @NamedQuery(name = TaxServiceCallResult.FIND_LAST_BY_TAXCALCULATION, query = TaxServiceCallResult.FIND_LAST_BY_TAXCALCULATION_QUERY)
})

@Entity
public class TaxServiceCallResult {
    public static final int HTTP_OK = 200;

    public static final String FIND_BY_TAXCALCULATION = "TaxServiceCallResult.FIND_BY_TAXCALCULATION";
    public static final String FIND_BY_TAXCALCULATION_QUERY = "SELECT tscr FROM TaxServiceCallResult tscr " +
            " WHERE tscr.taxCalculation.id = :taxCalculationId";

    public static final String GET_SUCCESS_SUM = "TaxServiceCallResult.GET_SUCCESS_SUM";
    public static final String GET_SUCCESS_SUM_QUERY = "SELECT SUM(tc.tax) FROM TaxServiceCallResult tscr" +
            " JOIN tscr.taxCalculation as tc " +
            " where tscr.responseStatus = " + HTTP_OK + " and tc.month = :month and tc.year = :year";

    public static final String GET_FAILED_SUM = "TaxServiceCallResult.GET_FAILED_SUM";
    public static final String GET_FAILED_SUM_QUERY = "SELECT SUM(tc.tax) FROM TaxServiceCallResult tscr" +
            " JOIN tscr.taxCalculation as tc " +
            " where tscr.responseStatus <> " + HTTP_OK + " and tc.month = :month and tc.year = :year";

    public static final String FIND_LAST_BY_TAXCALCULATION = "TaxServiceCallResult.FIND_LAST_BY_TAXCALCULATION";
    public static final String FIND_LAST_BY_TAXCALCULATION_QUERY = "SELECT tscr FROM TaxServiceCallResult tscr " +
            " WHERE tscr.taxCalculation.id = :taxCalculationId ORDER BY tscr.callDate DESC";

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @NotNull
    private TaxCalculation taxCalculation;

    @NotNull
    private String callParameters;

    @NotNull
    private int responseStatus;

    private String responseBody;

    private boolean successfulResponse;

    @JsonSerialize(using = JodaDateTimeSerializer.class)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @NotNull
    private DateTime callDate;

    public static TaxServiceCallResult from(TaxCalculation taxCalculation, String callParameters, int responseStatus, String responseBody, DateTime callDate, boolean successfulResponse) {
        TaxServiceCallResult taxServiceCallResult = new TaxServiceCallResult();
        taxServiceCallResult.taxCalculation = taxCalculation;
        taxServiceCallResult.callParameters = callParameters;
        taxServiceCallResult.responseStatus = responseStatus;
        taxServiceCallResult.responseBody = responseBody;
        taxServiceCallResult.callDate = callDate;
        taxServiceCallResult.successfulResponse = successfulResponse;
        return taxServiceCallResult;
    }

    public Long getId() {
        return id;
    }

    public TaxCalculation getTaxCalculation() {
        return taxCalculation;
    }

    public String getCallParameters() {
        return callParameters;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public DateTime getCallDate() {
        return callDate;
    }

    public boolean isSuccessfulResponse() {
        return successfulResponse;
    }

    public void setSuccessfulResponse(boolean successfulResponse) {
        this.successfulResponse = successfulResponse;
    }
}
