package be.cegeka.batchers.taxcalculator.application.service;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.domain.TaxCalculation;
import be.cegeka.batchers.taxcalculator.application.domain.TaxServiceCallResult;
import be.cegeka.batchers.taxcalculator.to.TaxServiceResponse;
import be.cegeka.batchers.taxcalculator.to.TaxTo;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.time.DateTime;
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

    @Autowired
    private String taxServiceUrl;

    @Autowired
    private RestTemplate restTemplate;

    public TaxServiceCallResult doWebserviceCallToTaxService(TaxCalculation taxCalculation) {
        try {
            ResponseEntity<TaxServiceResponse> webserviceResult = getWebserviceResult(taxCalculation.getEmployee());
            String status = webserviceResult.getBody().getStatus();
            if ("OK".equals(status)) {
                TaxTo taxTo = new TaxTo();
                taxTo.setAmount(taxCalculation.getEmployee().getIncomeTax());
                taxTo.setEmployeeId(taxCalculation.getEmployee().getId());

                TaxServiceCallResult taxServiceCallResult = TaxServiceCallResult.from(taxCalculation, taxTo.toString(),
                        HttpStatus.OK.value(), webserviceResult.toString(), DateTime.now());

                return taxServiceCallResult;
            }
            throw new TaxWebServiceException("Illegal response from web service");
        } catch (ResourceAccessException | HttpServerErrorException | HttpClientErrorException e) {
            throw new TaxWebServiceException("Could not retrieve response from webservice", e);
        }
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
