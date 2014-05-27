package be.cegeka.batchers.taxcalculator.presentation.rest;

import be.cegeka.batchers.taxcalculator.batch.service.JobResultsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class JobResultsControllerTest {

    @InjectMocks
    JobResultsController jobResultsController;

    @Mock
    JobResultsService jobResultsServiceMock;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(jobResultsController)
                .build();
    }

    @Test
    public void testGetResults() throws Exception {
        mockMvc.perform(get("/jobResults").contentType(MediaType.ALL))
                .andExpect(status().isOk())
                .andReturn();

        verify(jobResultsServiceMock).getFinishedJobResults();
    }
}