package be.cegeka.batchers.taxcalculator.application.service;

import be.cegeka.batchers.taxcalculator.application.domain.TaxCalculation;
import be.cegeka.batchers.taxcalculator.application.domain.TaxServiceCallResult;
import be.cegeka.batchers.taxcalculator.to.TaxServiceResponse;
import be.cegeka.batchers.taxcalculator.to.TaxTo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
public class TaxPaymentWebService {

    private static final Logger LOG = LoggerFactory.getLogger(TaxPaymentWebService.class);

    @Autowired
    private String taxServiceUrl;

    @Autowired
    private RestTemplate restTemplate;

    public TaxServiceCallResult doWebserviceCallToTaxService(TaxCalculation taxCalculation) {
        TaxTo taxTo = createWebserviceInput(taxCalculation);

        try {
            URI uri = URI.create(taxServiceUrl);
            ResponseEntity<TaxServiceResponse> webserviceResult = restTemplate.postForEntity(uri, taxTo, TaxServiceResponse.class);
            TaxServiceResponse taxServiceResponse = webserviceResult.getBody();

            if ("OK".equals(taxServiceResponse.getStatus())) {
                return getTaxServiceCallResult(taxCalculation, taxTo, webserviceResult.getStatusCode(), getJson(taxServiceResponse), true);
            } else {
                throw handleServerException(taxCalculation, taxTo, webserviceResult.getStatusCode(), getJson(taxServiceResponse));
            }
        } catch (HttpClientErrorException e) {
            throw handleClientException(taxCalculation, taxTo, e.getStatusCode(), e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            throw handleServerException(taxCalculation, taxTo, e.getStatusCode(), e.getResponseBodyAsString());
        } catch (ResourceAccessException e) {
            throw handleServerException(taxCalculation, taxTo, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private RuntimeException handleClientException(TaxCalculation taxCalculation, TaxTo taxTo, HttpStatus httpStatus, String responseBody) {
        return new TaxWebServiceFatalException(getTaxServiceCallResult(taxCalculation, taxTo, httpStatus, responseBody, false));
    }

    private RuntimeException handleServerException(TaxCalculation taxCalculation, TaxTo taxTo, HttpStatus httpStatus, String responseBody) {
        return new TaxWebServiceException(getTaxServiceCallResult(taxCalculation, taxTo, httpStatus, responseBody, false));
    }

    private TaxServiceCallResult getTaxServiceCallResult(TaxCalculation taxCalculation, TaxTo taxTo, HttpStatus httpStatus, String responseBody, boolean isSuccessfulResponse) {
        return TaxServiceCallResult.from(taxCalculation, getJson(taxTo), httpStatus.value(), responseBody, DateTime.now(), isSuccessfulResponse);
    }

    private String getJson(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private TaxTo createWebserviceInput(TaxCalculation taxCalculation) {
        TaxTo taxTo = new TaxTo();
        taxTo.setAmount(taxCalculation.getTax().getAmount().doubleValue());
        taxTo.setEmployeeId(taxCalculation.getEmployee().getId());
        return taxTo;
    }
}
