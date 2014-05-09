package be.cegeka.batchers.taxcalculator.batch;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.to.TaxTo;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.policy.TimeoutRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Component
public class CallWebserviceProcessor implements ItemProcessor<Employee, Employee> {

    @Autowired
    private String taxServiceUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Employee process(Employee employee) throws Exception {
        RetryTemplate template = new RetryTemplate();

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        template.setRetryPolicy(retryPolicy);

        return template.execute(new RetryCallback<Employee>() {
            public Employee doWithRetry(RetryContext context) {
                TaxTo taxTo = new TaxTo();
                taxTo.setAmount(employee.getIncomeTax());
                taxTo.setEmployeeId(String.valueOf(employee.getId()));
                ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity(URI.create(taxServiceUrl), taxTo, String.class);
                if ("OK" .equals(stringResponseEntity.getBody())) {
                    return employee;
                }
                throw new RuntimeException("Woops");
            }
        });
    }
}
