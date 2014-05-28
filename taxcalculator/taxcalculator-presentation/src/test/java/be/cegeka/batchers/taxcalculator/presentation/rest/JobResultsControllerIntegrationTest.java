package be.cegeka.batchers.taxcalculator.presentation.rest;

import be.cegeka.batchers.taxcalculator.presentation.rest.config.WebAppTestConfig;
import org.junit.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class JobResultsControllerIntegrationTest extends WebAppTestConfig {

    @Test
    public void testOK() throws Exception {
        mockMvc.perform(get("/files/job_report/123.pdf")
        ).andExpect(status().isOk())
                .andExpect(content().bytes(new byte[256]));

    }
}
