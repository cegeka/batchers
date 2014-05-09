package be.cegeka.batchers.taxcalculator.batch;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.to.TaxTo;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Arrays;

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
        taxTo.setEmployeeId(String.valueOf(employee.getId()));
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.TEXT_PLAIN));
        HttpEntity<TaxTo> entity = new HttpEntity<>(taxTo, headers);
        ResponseEntity<String> stringResponseEntity = restTemplate.exchange(URI.create(taxServiceUrl), HttpMethod.POST, entity, String.class);
        if ("OK".equals(stringResponseEntity.getBody()))
            return employee;
        throw new RuntimeException("Woops");
    }
}
