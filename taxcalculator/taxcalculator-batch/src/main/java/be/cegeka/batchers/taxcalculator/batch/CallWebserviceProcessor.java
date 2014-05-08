package be.cegeka.batchers.taxcalculator.batch;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
        ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity(URI.create(taxServiceUrl), null, String.class);
        if (stringResponseEntity.getBody().equals("OK")) return employee;
        throw new RuntimeException("Woops");
    }
}
