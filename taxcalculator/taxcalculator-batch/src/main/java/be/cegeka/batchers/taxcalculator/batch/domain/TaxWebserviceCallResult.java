package be.cegeka.batchers.taxcalculator.batch.domain;

import be.cegeka.batchers.taxcalculator.application.service.exceptions.TaxWebServiceException;
import be.cegeka.batchers.taxcalculator.application.util.jackson.JodaDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@NamedQueries({
        @NamedQuery(name = TaxWebserviceCallResult.FIND_BY_TAXCALCULATION, query = TaxWebserviceCallResult.FIND_BY_TAXCALCULATION_QUERY),
        @NamedQuery(name = TaxWebserviceCallResult.FIND_SUCCESSFUL_BY_TAXCALCULATION, query = TaxWebserviceCallResult.FIND_SUCCESSFUL_BY_TAXCALCULATION_QUERY),
})

@Entity
public class TaxWebserviceCallResult {

    public static final String FIND_BY_TAXCALCULATION = "TaxWebserviceCallResult.FIND_BY_TAXCALCULATION";
    public static final String FIND_BY_TAXCALCULATION_QUERY = "SELECT twcr FROM TaxWebserviceCallResult twcr " +
            " WHERE twcr.taxCalculation.id = :taxCalculationId";

    public static final String FIND_SUCCESSFUL_BY_TAXCALCULATION = "TaxWebserviceCallResult.FIND_SUCCESSFUL_BY_TAXCALCULATION";
    public static final String FIND_SUCCESSFUL_BY_TAXCALCULATION_QUERY = "SELECT twcr FROM TaxWebserviceCallResult twcr " +
            " WHERE twcr.taxCalculation.id = :taxCalculationId and twcr.successfulResponse IS true";

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @NotNull
    private TaxCalculation taxCalculation;

    @NotNull
    private int responseStatus;

    private String responseBody;

    private boolean successfulResponse;

    @JsonSerialize(using = JodaDateTimeSerializer.class)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @NotNull
    private DateTime callDate;

    public static TaxWebserviceCallResult callSucceeded(TaxCalculation taxCalculation) {
        TaxWebserviceCallResult taxWebserviceCallResult = new TaxWebserviceCallResult();
        taxWebserviceCallResult.taxCalculation = taxCalculation;
        taxWebserviceCallResult.responseStatus = 200;
        taxWebserviceCallResult.callDate = DateTime.now();
        taxWebserviceCallResult.successfulResponse = true;
        return taxWebserviceCallResult;
    }

    public static TaxWebserviceCallResult callFailed(TaxCalculation taxCalculation, TaxWebServiceException taxWebServiceException) {
        TaxWebserviceCallResult taxWebserviceCallResult = new TaxWebserviceCallResult();
        taxWebserviceCallResult.taxCalculation = taxCalculation;
        taxWebserviceCallResult.responseStatus = taxWebServiceException.getHttpStatus().value();
        taxWebserviceCallResult.responseBody = taxWebServiceException.getResponseBody();
        taxWebserviceCallResult.callDate = DateTime.now();
        taxWebserviceCallResult.successfulResponse = false;
        return taxWebserviceCallResult;
    }

    public Long getId() {
        return id;
    }

    public TaxCalculation getTaxCalculation() {
        return taxCalculation;
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
