package be.cegeka.batchers.taxcalculator.batch;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.to.TaxServiceResponse;
import be.cegeka.batchers.taxcalculator.to.TaxTo;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Component
public class CallWebserviceProcessor implements ItemProcessor<Employee, Employee> {

    @Autowired
    String taxServiceUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Employee process(Employee employee) throws Exception {
        TaxTo taxTo = new TaxTo();
        taxTo.setAmount(employee.getIncomeTax());
        taxTo.setEmployeeId(employee.getId());
        ResponseEntity<TaxServiceResponse> responseEntity = restTemplate.postForEntity(URI.create(taxServiceUrl), taxTo, TaxServiceResponse.class);
        if ("OK".equals(responseEntity.getBody().status))
            return employee;
        throw new RuntimeException("Woops");
    }
}
