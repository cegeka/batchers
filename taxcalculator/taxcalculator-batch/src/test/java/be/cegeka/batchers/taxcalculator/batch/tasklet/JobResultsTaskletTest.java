package be.cegeka.batchers.taxcalculator.batch.tasklet;

import be.cegeka.batchers.taxcalculator.batch.service.reporting.MonthlyTaxReportService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

@RunWith(MockitoJUnitRunner.class)
public class JobResultsTaskletTest {
    public static final long JOB_EXECUTION_ID = 12345l;
    @InjectMocks
    JobResultsTasklet jobResultsTasklet;
    @Mock
    StepContribution stepContributionMock;
    @Mock
    ChunkContext chunkContextMock;
    @Mock
    MonthlyTaxReportService monthlyTaxReportServiceMock;

    @Test
    public void testExecuteCallsService() throws Exception {
        when(chunkContextMock.getStepContext()).thenReturn(new StepContext(new StepExecution("stepName", new JobExecution(JOB_EXECUTION_ID), 123L)));

        setInternalState(jobResultsTasklet, "month", 2L);
        setInternalState(jobResultsTasklet, "year", 2015L);

        jobResultsTasklet.execute(stepContributionMock, chunkContextMock);

        verify(monthlyTaxReportServiceMock).generateReport(12345L, 2015, 2);
    }
}