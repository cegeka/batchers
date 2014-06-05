package be.cegeka.batchers.taxcalculator.batch.service;

import be.cegeka.batchers.taxcalculator.batch.domain.JobResult;
import be.cegeka.batchers.taxcalculator.batch.integration.AbstractIntegrationTest;
import org.joda.time.DateTimeUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class JobResultsServiceITest extends AbstractIntegrationTest {
    @Autowired
    private JobResultsService jobResultsService;
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @BeforeClass
    public static void setUp() {
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void testJobResults() throws Exception {
        int year = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();
        int previousMonth = currentMonth - 1;

        jobLauncherTestUtils.launchJob(getJobParametersForMonth(currentMonth, year));
        jobLauncherTestUtils.launchJob(getJobParametersForMonth(previousMonth, year));

        List<JobResult> finishedJobResults = jobResultsService.getJobResults();

        assertThat(finishedJobResults)
                .isNotEmpty()
                .hasSize(6);

        assertThat(finishedJobResults.get(0).getJobStartParams().getMonth()).isEqualTo(currentMonth);
        assertThat(finishedJobResults.get(0).getJobExecutionResults()).hasSize(1);
    }

    private JobParameters getJobParametersForMonth(long month, long year) {
        return new JobParametersBuilder()
                .addLong("year", year, true)
                .addLong("month", month, true)
                .toJobParameters();
    }
}