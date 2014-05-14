package be.cegeka.batchers.taxcalculator.application.service;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.to.TaxServiceResponse;
import be.cegeka.batchers.taxcalculator.to.TaxTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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

    public Employee doWebserviceCallToTaxService(Employee employee) {
        try {
            if ("OK".equals(getWebserviceResult(employee))) {
                return employee;
            }
            throw new TaxWebServiceException("Illegal response from web service");
        } catch (ResourceAccessException | HttpServerErrorException e) {
            throw new TaxWebServiceException("Could not retrieve response from webservice", e);
        }
    }

    private String getWebserviceResult(Employee employee) {
        ResponseEntity<TaxServiceResponse> stringResponseEntity = restTemplate.postForEntity(getUri(), createWebserviceInput(employee), TaxServiceResponse.class);
        return stringResponseEntity.getBody().status;
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
