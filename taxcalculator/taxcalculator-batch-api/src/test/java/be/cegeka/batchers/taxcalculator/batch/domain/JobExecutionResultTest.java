package be.cegeka.batchers.taxcalculator.batch.domain;

import org.junit.Test;

import java.util.Date;

import static org.fest.assertions.api.Assertions.assertThat;

public class JobExecutionResultTest {

    @Test
    public void givenStartAndEndTime_whenGetDuration_thenCalculateDurationAsDifference() {
        JobExecutionResult jobExecutionResult = new JobExecutionResult(1234567L, "COMPLETED", new Date(0), new Date(1000), "1", null);
        assertThat(jobExecutionResult.getDuration()).isEqualTo(1000);
    }

    @Test
    public void givenRunningJobExecutionWithoutEndTime_whenGetDuration_thenReturnNull() {
        JobExecutionResult jobExecutionResult = new JobExecutionResult(1234567L, "STARTED", new Date(0), null, "1", null);
        assertThat(jobExecutionResult.getDuration()).isNull();
    }

    @Test
    public void givenNotStartedJobExecution_whenGetDuration_thenReturnNull() {
        JobExecutionResult jobExecutionResult = new JobExecutionResult(1234567L, "STARTED", null, null, "1", null);
        assertThat(jobExecutionResult.getDuration()).isNull();
    }
}