package be.cegeka.batchers.taxcalculator.batch.service;

import be.cegeka.batchers.taxcalculator.batch.api.JobStartListener;
import be.cegeka.batchers.taxcalculator.to.TaxServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Component
public class ResetStubWebserviceService implements JobStartListener {

    private static Logger LOG = LoggerFactory.getLogger(ResetStubWebserviceService.class);

    @Autowired
    private String resetUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void jobHasBeenStarted(String jobName) {
        try {
            restTemplate.postForEntity(getUri(), null, Void.class);
            LOG.info("Resetted the StubWebservice successful on restart");
        } catch(RestClientException e) {
            LOG.info("Failed to reset the StubWebservice on restart");
            throw new IllegalStateException("Error resetting the StubWebservice... results may be unpredictable.", e);
        }
    }

    private URI getUri() {
        return URI.create(resetUrl);
    }

}
