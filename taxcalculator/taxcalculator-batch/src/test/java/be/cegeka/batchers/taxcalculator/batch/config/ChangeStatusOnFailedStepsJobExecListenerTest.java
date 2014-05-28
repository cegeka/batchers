package be.cegeka.batchers.taxcalculator.batch.config;

import org.junit.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;

import static java.util.Arrays.asList;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.springframework.batch.core.ExitStatus.*;

public class ChangeStatusOnFailedStepsJobExecListenerTest {
    private ChangeStatusOnFailedStepsJobExecListener changeStatusOnFailedStepsJobExecListener = new ChangeStatusOnFailedStepsJobExecListener();

    @Test
    public void testAfterJobJobExecutionHasBatchStatusFailedWhenWeHaveStepsWithStatusNotCompleted() throws Exception {
        JobExecution jobExecution = new JobExecution(1L);

        StepExecution failedStepExecution = createStepExecution(jobExecution, FAILED);
        StepExecution completedStepExecution = createStepExecution(jobExecution, COMPLETED);
        StepExecution stoppedStepExecution = createStepExecution(jobExecution, STOPPED);

        jobExecution.addStepExecutions(asList(failedStepExecution, completedStepExecution, stoppedStepExecution));

        changeStatusOnFailedStepsJobExecListener.afterJob(jobExecution);

        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.FAILED);
    }

    @Test
    public void testAfterJobJobExecutionHasBatchStatusUnchangedWhenAllStepsCompleted() throws Exception {
        JobExecution jobExecution = new JobExecution(1L);

        StepExecution completedStepExecution = createStepExecution(jobExecution, COMPLETED);
        StepExecution anotherStepExecution = createStepExecution(jobExecution, COMPLETED);

        jobExecution.addStepExecutions(asList(completedStepExecution, anotherStepExecution));
        BatchStatus jobExecutionStatus = jobExecution.getStatus();

        changeStatusOnFailedStepsJobExecListener.afterJob(jobExecution);

        assertThat(jobExecution.getStatus()).isEqualTo(jobExecutionStatus);
    }

    private StepExecution createStepExecution(JobExecution jobExecution, ExitStatus status) {
        StepExecution stepExecution = new StepExecution(status.toString(), jobExecution);
        stepExecution.setExitStatus(status);
        return stepExecution;
    }
}