package be.cegeka.batchers.taxcalculator.presentation.rest;

import be.cegeka.batchers.taxcalculator.application.domain.reporting.MonthlyReport;
import be.cegeka.batchers.taxcalculator.application.domain.reporting.MonthlyReportRepository;
import be.cegeka.batchers.taxcalculator.batch.service.JobResultsService;
import be.cegeka.batchers.taxcalculator.presentation.rest.controller.JobResultsController;
import org.joda.time.DateTime;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class JobResultsControllerTest {

    public static final long JOB_EXECUTION_ID = 112L;
    @InjectMocks
    JobResultsController jobResultsController;

    @Mock
    JobResultsService jobResultsServiceMock;
    @Mock
    MonthlyReportRepository monthlyReportRepository;

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

        verify(jobResultsServiceMock).getJobResults();
    }

    @Test
    public void testGetReportContent() throws Exception {
        byte[] bytes = new byte[]{0, 34, 4};
        MonthlyReport monthlyReport = MonthlyReport.from(JOB_EXECUTION_ID, 2014, 4, bytes, new DateTime());
        when(monthlyReportRepository.findById(JOB_EXECUTION_ID))
                .thenReturn(monthlyReport);

        mockMvc.perform(get("/files/job_report/" + JOB_EXECUTION_ID + ".pdf")).andExpect(status().isOk())
                .andExpect(content().bytes(bytes));
    }
}