package be.cegeka.batchers.taxcalculator.application.service;

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
        TaxTo taxTo = createWebserviceInput(taxCalculation);
        Integer httpStatus;
        String responseBody;
        boolean isSuccessfulResponse = false;

        try {
            URI uri = URI.create(taxServiceUrl);
            ResponseEntity<TaxServiceResponse> webserviceResult = restTemplate.postForEntity(uri, taxTo, TaxServiceResponse.class);
            TaxServiceResponse taxServiceResponse = webserviceResult.getBody();

            if (taxServiceResponse.getStatus().equals("OK")){
                isSuccessfulResponse = true;
            }

            httpStatus = webserviceResult.getStatusCode().value();
            responseBody = taxServiceResponse.toString();
        } catch (HttpStatusCodeException e){
            httpStatus = e.getStatusCode().value();
            responseBody = e.getResponseBodyAsString();
        } catch (ResourceAccessException e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR.value();
            responseBody = e.getMessage();
        }

        TaxServiceCallResult taxServiceCallResult = TaxServiceCallResult.from(taxCalculation, taxTo.toString(),
                httpStatus, responseBody, DateTime.now(), isSuccessfulResponse);

        if (!taxServiceCallResult.isSuccessfulResponse()) {
            throw new TaxWebServiceException(taxServiceCallResult);
        } else {
            return taxServiceCallResult;
        }
    }

    private TaxTo createWebserviceInput(TaxCalculation taxCalculation) {
        TaxTo taxTo = new TaxTo();
        taxTo.setAmount(taxCalculation.getTax().getAmount().doubleValue());
        taxTo.setEmployeeId(taxCalculation.getEmployee().getId());
        return taxTo;
    }
}
