package be.cegeka.batchers.taxcalculator.application.domain;

import be.cegeka.batchers.taxcalculator.application.util.jackson.JodaDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@NamedQueries({
        @NamedQuery(name = TaxServiceCallResult.FIND_BY_TAXCALCULATION, query = TaxServiceCallResult.FIND_BY_TAXCALCULATION_QUERY)
})

@Entity
public class TaxServiceCallResult {

    public static final String FIND_BY_TAXCALCULATION = "TaxServiceCallResult.FIND_BY_TAXCALCULATION";
    public static final String FIND_BY_TAXCALCULATION_QUERY = "SELECT tscr FROM TaxServiceCallResult tscr " +
            " WHERE tscr.taxCalculation.id = :taxCalculationId";

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

    @JsonSerialize(using = JodaDateTimeSerializer.class)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @NotNull
    private DateTime callDate;

    public static TaxServiceCallResult from(TaxCalculation taxCalculation, String callParameters, int responseStatus, String responseBody, DateTime callDate) {
        TaxServiceCallResult taxServiceCallResult = new TaxServiceCallResult();
        taxServiceCallResult.taxCalculation = taxCalculation;
        taxServiceCallResult.callParameters = callParameters;
        taxServiceCallResult.responseStatus = responseStatus;
        taxServiceCallResult.responseBody = responseBody;
        taxServiceCallResult.callDate = callDate;
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
}
