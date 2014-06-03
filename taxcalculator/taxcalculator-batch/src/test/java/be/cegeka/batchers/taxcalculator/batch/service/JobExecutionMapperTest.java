package be.cegeka.batchers.taxcalculator.batch.service;

import be.cegeka.batchers.taxcalculator.batch.domain.JobExecutionResult;
import be.cegeka.batchers.taxcalculator.batch.domain.JobResult;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;


@RunWith(MockitoJUnitRunner.class)
public class JobExecutionMapperTest {
    public static final long JOB_ID = 122L;
    public static final String JOB_NAME = "JOB NAME";
    private static final Long JOB_EXECUTION_ID = 222L;
    @InjectMocks
    JobExecutionMapper mapper;
    private int DURATION_IN_SECONDS = 5;

    @Test
    public void testToJobResultTo() throws Exception {
        //ARRANGE
        Date now = DateTime.now().toDate();
        JobInstance jobInstance = new JobInstance(JOB_ID, JOB_NAME);
        JobParameters jobParams = new JobParameters();

        JobExecution jobExecution = new JobExecution(jobInstance, jobParams);
        jobExecution.setStatus(BatchStatus.ABANDONED);
        jobExecution.setStartTime(new DateTime(now).minusSeconds(DURATION_IN_SECONDS).toDate());
        jobExecution.setEndTime(now);
        jobExecution.setId(JOB_EXECUTION_ID);
        Map.Entry<JobInstance, List<JobExecution>> entry = new Map.Entry<JobInstance, List<JobExecution>>() {
            @Override
            public JobInstance getKey() {
                return jobInstance;
            }

            @Override
            public List<JobExecution> getValue() {
                return Arrays.asList(jobExecution);
            }

            @Override
            public List<JobExecution> setValue(List<JobExecution> value) {
                return null;
            }

        };

        //ACT
        JobResult resultTo = mapper.toJobResult(entry);

        //ASSERT
        assertThat(resultTo.getJobExecutionResults()).hasSize(1);
        JobExecutionResult jobExecutionResult = resultTo.getJobExecutionResults().get(0);
        assertThat(jobExecutionResult.getStatus()).isEqualTo(BatchStatus.ABANDONED.toString());
        assertThat(jobExecutionResult.getDuration()).isEqualTo(DURATION_IN_SECONDS * 1000);
    }
}