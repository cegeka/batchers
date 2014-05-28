package be.cegeka.batchers.taxcalculator.batch.tasklet;

import be.cegeka.batchers.taxcalculator.batch.service.reporting.MonthlyTaxReportService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class JobResultsTaskletTest {
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
        Whitebox.setInternalState(jobResultsTasklet, "month", 2);
        Whitebox.setInternalState(jobResultsTasklet, "year", 2015);

        jobResultsTasklet.execute(stepContributionMock, chunkContextMock);

        verify(monthlyTaxReportServiceMock).generateReport(2015, 2);
    }
}