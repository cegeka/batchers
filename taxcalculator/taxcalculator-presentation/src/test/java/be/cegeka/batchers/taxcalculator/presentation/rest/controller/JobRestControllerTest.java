package be.cegeka.batchers.taxcalculator.presentation.rest.controller;

import be.cegeka.batchers.taxcalculator.batch.api.JobService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class JobRestControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private JobRestController jobRestController;

    @Mock
    private JobService jobService;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(jobRestController)
                .build();
    }

    @Test
    public void givenBatch_whenRunJob_thenServiceIsCalled() throws Exception {
        mockMvc.perform(post("/runJob").contentType(MediaType.ALL))
                .andExpect(status().isOk())
                .andReturn();
        verify(jobService, times(1)).runTaxCalculatorJob();
    }
}