package be.cegeka.batchers.taxcalculator.batch.domain;

import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.fest.assertions.api.Assertions.assertThat;

public class JobResultTest {

    @Test
    public void testSortingOfJobExecutionResults() throws Exception {
        JobExecutionResult execution5 = jobExecutionResult(5L);
        JobExecutionResult execution2 = jobExecutionResult(2L);
        JobExecutionResult execution19 = jobExecutionResult(19L);
        List<JobExecutionResult> executions = asList(execution5, execution2, execution19);

        JobResult jobResult = new JobResult(null, null, executions);

        assertThat(jobResult.getJobExecutionResults()).containsExactly(execution19, execution5, execution2);
    }

    private JobExecutionResult jobExecutionResult(Long id) {
        return new JobExecutionResult(id, null, null, null, null);
    }
}