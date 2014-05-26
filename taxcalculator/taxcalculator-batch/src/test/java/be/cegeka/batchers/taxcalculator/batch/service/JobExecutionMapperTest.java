package be.cegeka.batchers.taxcalculator.batch.service;

import be.cegeka.batchers.taxcalculator.to.JobResultTo;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;

import java.util.Date;

import static org.fest.assertions.api.Assertions.assertThat;


@RunWith(MockitoJUnitRunner.class)
public class JobExecutionMapperTest {
    public static final long JOB_ID = 122L;
    public static final String JOB_NAME = "JOB NAME";
    private static final Long JOB_EXECUTION_ID = 222L;
    @InjectMocks
    JobExecutionMapper mapper;

    @Test
    public void testToJobResultTo() throws Exception {
        Date now = DateTime.now().toDate();

        JobInstance jobInstance = new JobInstance(JOB_ID, JOB_NAME);
        JobParameters jobParams = new JobParameters();

        JobExecution jobExecution = new JobExecution(jobInstance, jobParams);
        jobExecution.setStatus(BatchStatus.ABANDONED);
        jobExecution.setCreateTime(now);
        jobExecution.setId(JOB_EXECUTION_ID);

        JobResultTo resultTo = mapper.toJobResultTo(jobExecution);
        assertThat(resultTo.getStatus()).isEqualTo(BatchStatus.ABANDONED.toString());
        assertThat(resultTo.getDateTime()).isEqualTo(now);
        assertThat(resultTo.getExecutionId()).isEqualTo(JOB_NAME + " " + JOB_EXECUTION_ID);
    }
}