package be.cegeka.batchers.taxcalculator.batch.service;

import be.cegeka.batchers.taxcalculator.batch.integration.AbstractBatchIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.springframework.test.web.client.MockRestServiceServer.createServer;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class ResetStubWebserviceServiceITest extends AbstractBatchIntegrationTest {

    @Autowired
    private ResetStubWebserviceService resetStubWebserviceService;

    private MockRestServiceServer mockServer;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private String resetUrl;

    @Before
    public void setUp() {
        mockServer = createServer(restTemplate);
    }

    @Test
    public void resetStubWebservice() {
        mockServer.expect(requestTo(resetUrl)).andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess());

        resetStubWebserviceService.jobHasBeenStarted("job");

        mockServer.verify();
    }

    @Test(expected = IllegalStateException.class)
    public void resetStubWebservice_ErrorOccurs_IllegalStateException() {
        mockServer.expect(requestTo(resetUrl)).andExpect(method(HttpMethod.POST))
                .andRespond(withServerError());

        resetStubWebserviceService.jobHasBeenStarted("job");

    }

}
