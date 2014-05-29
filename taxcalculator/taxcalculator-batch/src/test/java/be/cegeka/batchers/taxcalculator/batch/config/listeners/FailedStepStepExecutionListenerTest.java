package be.cegeka.batchers.taxcalculator.batch.config.listeners;

import be.cegeka.batchers.taxcalculator.batch.config.listeners.FailedStepStepExecutionListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class FailedStepStepExecutionListenerTest {

    @InjectMocks
    private FailedStepStepExecutionListener failedStepStepExecutionListener;

    @Mock
    private JobExecution jobExecution;
    private StepExecution stepExecution = new StepExecution("step", jobExecution);

    @Test
    public void testAfterStep_returns_FailedExitStatus_whenRead_And_Write_Count_Dont_Match() throws Exception {
        stepExecution.setReadCount(2);
        stepExecution.setWriteCount(1);

        ExitStatus exitStatus = failedStepStepExecutionListener.afterStep(stepExecution);

        assertThat(exitStatus).isEqualTo(ExitStatus.FAILED);
    }

    @Test
    public void testAfterStep_returns_CompletedExitStatus_whenRead_And_Write_Match() throws Exception {
        stepExecution.setReadCount(2);
        stepExecution.setWriteCount(2);

        ExitStatus exitStatus = failedStepStepExecutionListener.afterStep(stepExecution);

        assertThat(exitStatus).isEqualTo(ExitStatus.COMPLETED);
    }
}