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
        TaxTo taxTo = new TaxTo();
        taxTo.setAmount(taxCalculation.getEmployee().getIncomeTax());
        taxTo.setEmployeeId(taxCalculation.getEmployee().getId());

        TaxServiceCallResult taxServiceCallResult;
        int httpStatus;
        String responseBody;

        try {
            ResponseEntity<TaxServiceResponse> webserviceResult = getWebserviceResult(taxCalculation.getEmployee());
            String status = webserviceResult.getBody().getStatus();

            if ("OK".equals(status)) {
                httpStatus = HttpStatus.OK.value();
            } else {
                httpStatus = webserviceResult.getStatusCode().value();
            }

            responseBody = webserviceResult.getBody().toString();
        } catch (HttpStatusCodeException e){
            httpStatus = e.getStatusCode().value();
            responseBody = e.getResponseBodyAsString();
        } catch (ResourceAccessException e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR.value();
            responseBody = e.getMessage();
        }

        taxServiceCallResult = getTaxServiceCallResult(taxCalculation, taxTo,
                httpStatus, responseBody);
        return taxServiceCallResult;
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
