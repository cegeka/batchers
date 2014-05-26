package be.cegeka.batchers.taxcalculator.application.service;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.domain.TaxCalculation;
import be.cegeka.batchers.taxcalculator.application.domain.TaxServiceCallResult;
import be.cegeka.batchers.taxcalculator.to.TaxServiceResponse;
import be.cegeka.batchers.taxcalculator.to.TaxTo;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;

import java.net.URI;

@Service
public class TaxPaymentWebService {

    @Autowired
    private String taxServiceUrl;

    @Autowired
    private RestTemplate restTemplate;

    public TaxServiceCallResult doWebserviceCallToTaxService(TaxCalculation taxCalculation) {
        TaxServiceCallResult taxServiceCallResult;

        try {
            ResponseEntity<TaxServiceResponse> webserviceResult = getWebserviceResult(taxCalculation.getEmployee());
            taxServiceCallResult = buildTaxServiceCallResult(webserviceResult, taxCalculation);
        } catch (HttpStatusCodeException e){
            taxServiceCallResult = buildTaxServiceCallResult(e, taxCalculation);
        } catch (ResourceAccessException e) {
            taxServiceCallResult = buildTaxServiceCallResult(e, taxCalculation);
        }

        if (!taxServiceCallResult.isHttpStatusOk()) {
            throw new TaxWebServiceException(taxServiceCallResult);
        } else {
            return taxServiceCallResult;
        }
    }

    private TaxServiceCallResult buildTaxServiceCallResult(ResourceAccessException e, TaxCalculation taxCalculation) {
        TaxTo taxTo = getTaxTo(taxCalculation);
        int httpStatus = HttpStatus.INTERNAL_SERVER_ERROR.value();

        String responseBody = e.getMessage();
        TaxServiceCallResult taxServiceCallResult = getTaxServiceCallResult(taxCalculation, taxTo,
                httpStatus, responseBody);

        return taxServiceCallResult;
    }

    private TaxServiceCallResult buildTaxServiceCallResult(HttpStatusCodeException e, TaxCalculation taxCalculation) {
        TaxTo taxTo = getTaxTo(taxCalculation);

        int httpStatus = e.getStatusCode().value();
        String responseBody = e.getResponseBodyAsString();
        TaxServiceCallResult taxServiceCallResult = getTaxServiceCallResult(taxCalculation, taxTo,
                httpStatus, responseBody);

        return taxServiceCallResult;
    }

    private TaxServiceCallResult buildTaxServiceCallResult(ResponseEntity<TaxServiceResponse> webserviceResult, TaxCalculation taxCalculation) {
        String status = webserviceResult.getBody().getStatus();
        Integer httpStatus;

        if ("OK".equals(status)) {
            httpStatus = HttpStatus.OK.value();
        } else {
            HttpStatus wsResultStatusCode = webserviceResult.getStatusCode();
            if (wsResultStatusCode != null) {
                httpStatus = wsResultStatusCode.value();
            } else {
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR.value();
            }
        }

        TaxTo taxTo = getTaxTo(taxCalculation);

        String responseBody = webserviceResult.getBody().toString();
        TaxServiceCallResult taxServiceCallResult = getTaxServiceCallResult(taxCalculation, taxTo,
                httpStatus, responseBody);
        return taxServiceCallResult;
    }

    private TaxTo getTaxTo(TaxCalculation taxCalculation) {
        TaxTo taxTo = new TaxTo();
        taxTo.setAmount(taxCalculation.getEmployee().getIncomeTax());
        taxTo.setEmployeeId(taxCalculation.getEmployee().getId());
        return taxTo;
    }

    private TaxServiceCallResult getTaxServiceCallResult(TaxCalculation taxCalculation, TaxTo taxTo, int httpStatusCode, String responseBody) {
        return TaxServiceCallResult.from(taxCalculation, taxTo.toString(),
                httpStatusCode, responseBody, DateTime.now());
    }

    private ResponseEntity<TaxServiceResponse> getWebserviceResult(Employee employee) {
        ResponseEntity<TaxServiceResponse> stringResponseEntity = restTemplate.postForEntity(getUri(), createWebserviceInput(employee), TaxServiceResponse.class);
        return stringResponseEntity;
    }

    private URI getUri() {
        return URI.create(taxServiceUrl);
    }

    private TaxTo createWebserviceInput(Employee employee) {
        TaxTo taxTo = new TaxTo();
        taxTo.setAmount(employee.getIncomeTax());
        taxTo.setEmployeeId(employee.getId());
        return taxTo;
    }
}
