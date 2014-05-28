package be.cegeka.batchers.taxcalculator.batch.domain;

import org.junit.Test;

import java.util.Calendar;
import java.util.List;

import static java.util.Arrays.asList;
import static org.fest.assertions.api.Assertions.assertThat;

public class JobResultTest {

    @Test
    public void testSortingOfJobExecutionResults() throws Exception {
        JobExecutionResult je5 = jobExecutionResult(5);
        JobExecutionResult je2 = jobExecutionResult(2);
        JobExecutionResult je19 = jobExecutionResult(19);
        List<JobExecutionResult> input = asList(je5, je2, je19);

        JobResult jobResult = new JobResult(null, null, input);

        assertThat(jobResult.getJobExecutionResults()).containsExactly(je19, je5, je2);
    }

    private JobExecutionResult jobExecutionResult(int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, day);

        JobExecutionResult jobExecutionResult = new JobExecutionResult(null, null, calendar.getTime(), null);
        return jobExecutionResult;
    }
}