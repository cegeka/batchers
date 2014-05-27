package be.cegeka.batchers.taxcalculator.batch.config;

import be.cegeka.batchers.taxcalculator.application.service.TaxWebServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RetryConfig {

    @Value("${taxProcessor.retry.initialInterval:100}")
    private long initialInterval = 100;

    @Value("${taxProcessor.retry.maxAtempts:3}")
    private int maxAtempts = 3;

    public RetryTemplate createRetryTemplate() {
        Map<Class<? extends Throwable>, Boolean> exceptions = new HashMap<>();
        exceptions.put(TaxWebServiceException.class, true);

        RetryTemplate template = new RetryTemplate();
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(maxAtempts, exceptions);
        template.setRetryPolicy(retryPolicy);

        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(initialInterval);
        template.setBackOffPolicy(backOffPolicy);

        return template;
    }
}
