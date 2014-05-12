package be.cegeka.batchers.taxcalculator.batch;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.service.TaxPaymentWebService;
import be.cegeka.batchers.taxcalculator.application.service.TaxWebServiceException;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CallWebserviceProcessor implements ItemProcessor<Employee, Employee> {

    @Autowired
    private TaxPaymentWebService taxPaymentWebService;

    @Value("${taxProcessor.retry.initialInterval}")
    private long initialInterval;

    @Value("${taxProcessor.retry.maxAtempts}")
    private int maxAtempts;

    @Override
    public Employee process(Employee employee) throws Exception {
        return createRetryTemplate().execute(retryContext -> taxPaymentWebService.doWebserviceCallToTaxService(employee));
    }

    private RetryTemplate createRetryTemplate() {
        Map<Class<? extends Throwable>, Boolean> exceptions = new HashMap<>();
        exceptions.put(TaxWebServiceException.class, true);

        RetryTemplate template = new RetryTemplate();
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(maxAtempts!=0?maxAtempts:3, exceptions);
        template.setRetryPolicy(retryPolicy);

        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(initialInterval!=0?initialInterval:1000);
        template.setBackOffPolicy(backOffPolicy);

        return template;
    }
}
