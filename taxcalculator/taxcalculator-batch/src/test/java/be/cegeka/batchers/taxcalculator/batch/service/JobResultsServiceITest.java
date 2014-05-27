package be.cegeka.batchers.taxcalculator.batch.service;

import be.cegeka.batchers.taxcalculator.batch.domain.JobResult;
import be.cegeka.batchers.taxcalculator.batch.integration.AbstractIntegrationTest;
import org.junit.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class JobResultsServiceITest extends AbstractIntegrationTest {
    @Autowired
    private JobResultsService jobResultsService;
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void testJobResults() throws Exception {
        jobLauncherTestUtils.launchJob(getJobParametersForMonth(5L));
        jobLauncherTestUtils.launchJob(getJobParametersForMonth(6L));

        List<JobResult> finishedJobResults = jobResultsService.getFinishedJobResults();

        assertThat(finishedJobResults).isNotEmpty();

//        assertThat(finishedJobResults.get(0).getJobName()).isEqualTo(EmployeeJobConfig.EMPLOYEE_JOB);
//        assertThat(finishedJobResults.get(0).getJobStartParams().getYear()).isEqualTo(2014);
//        assertThat(finishedJobResults.get(0).getJobStartParams().getMonth()).isEqualTo(5);
//        assertThat(finishedJobResults.get(0).getJobExecutionResults()).hasSize(1);
    }

    private JobParameters getJobParametersForMonth(long month) {
        return new JobParametersBuilder()
                .addLong("year", 2014L, true)
                .addLong("month", month, true)
                .toJobParameters();
    }
}