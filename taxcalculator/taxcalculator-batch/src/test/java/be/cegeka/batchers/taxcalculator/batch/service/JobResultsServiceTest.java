package be.cegeka.batchers.taxcalculator.batch.service;


import be.cegeka.batchers.taxcalculator.batch.config.EmployeeJobConfig;
import be.cegeka.batchers.taxcalculator.batch.domain.JobResult;
import be.cegeka.batchers.taxcalculator.batch.domain.JobStartParams;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.lang.Integer.MAX_VALUE;
import static java.util.Arrays.asList;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JobResultsServiceTest {
    @InjectMocks
    JobResultsService jobResultsService;

    @Mock
    JobExplorer jobExplorer;

    JobExecutionMapper mapper = new JobExecutionMapper();


    @Before
    public void setUp() {
        Whitebox.setInternalState(jobResultsService, "jobExecutionMapper", mapper);

    }

    @Test
    public void testGetFinishedJobResults_DifferentDates_SortingIsDescOnYearAndMonth() throws Exception {
        //ARRANGE
        JobInstance jobInstance1 = new JobInstance(1L, EmployeeJobConfig.EMPLOYEE_JOB);
        JobInstance jobInstance2 = new JobInstance(2L, EmployeeJobConfig.EMPLOYEE_JOB);
        List<JobInstance> jobInstances = asList(jobInstance1, jobInstance2);

        when(jobExplorer.getJobInstancesByJobName(EmployeeJobConfig.EMPLOYEE_JOB, 0, MAX_VALUE))
                .thenReturn(jobInstances);

        JobExecution jobInstance1_jobExecution1 = createJobExecution(jobInstance1, createJobParameters(2014, 5));
        when(jobExplorer.getJobExecutions(jobInstance1)).thenReturn(asList(jobInstance1_jobExecution1));

        JobExecution jobInstance2_jobExecution1 = createJobExecution(jobInstance2, createJobParameters(2013, 6));
        JobExecution jobInstance2_jobExecution2 = createJobExecution(jobInstance2, createJobParameters(2013, 6));
        when(jobExplorer.getJobExecutions(jobInstance2)).thenReturn(asList(jobInstance2_jobExecution1, jobInstance2_jobExecution2));

        //ACT
        List<JobResult> jobResults = jobResultsService.getFinishedJobResults();

        //ASSERT
        verify(jobExplorer).getJobInstancesByJobName(EmployeeJobConfig.EMPLOYEE_JOB, 0, MAX_VALUE);
        verify(jobExplorer).getJobExecutions(jobInstance1);
        verify(jobExplorer).getJobExecutions(jobInstance2);

        assertThat(jobResults).hasSize(2);
        assertThat(jobResults.get(0).getJobStartParams().getYear()).isEqualTo(2014);
        assertThat(jobResults.get(0).getJobStartParams().getMonth()).isEqualTo(5);
        assertThat(jobResults.get(1).getJobStartParams().getYear()).isEqualTo(2013);
        assertThat(jobResults.get(1).getJobStartParams().getMonth()).isEqualTo(6);
    }

    @Test
    public void testGetFinishedJobResults_SameDates_SortingIsDescOnDate() throws Exception {
        //ARRANGE
        JobInstance jobInstance1 = new JobInstance(1L, EmployeeJobConfig.EMPLOYEE_JOB);
        JobInstance jobInstance2 = new JobInstance(2L, EmployeeJobConfig.EMPLOYEE_JOB);
        List<JobInstance> jobInstances = asList(jobInstance1, jobInstance2);

        when(jobExplorer.getJobInstancesByJobName(EmployeeJobConfig.EMPLOYEE_JOB, 0, MAX_VALUE))
                .thenReturn(jobInstances);

        JobExecution jobInstance1_jobExecution1 = new JobExecution(jobInstance1, createJobParameters(2014, 6));
        jobInstance1_jobExecution1.setEndTime(getDateOfDay(3));
        when(jobExplorer.getJobExecutions(jobInstance1)).thenReturn(asList(jobInstance1_jobExecution1));

        JobExecution jobInstance2_jobExecution1 = new JobExecution(jobInstance2, createJobParameters(2014, 6));
        jobInstance2_jobExecution1.setEndTime(getDateOfDay(4));
        when(jobExplorer.getJobExecutions(jobInstance2)).thenReturn(asList(jobInstance2_jobExecution1));

        //ACT
        List<JobResult> jobResults = jobResultsService.getFinishedJobResults();

        assertThat(jobResults.get(0).getJobExecutionResults().get(0).getDateTime()).isAfter(jobResults.get(1).getJobExecutionResults().get(0).getDateTime());
    }

    private JobExecution createJobExecution(JobInstance jobInstance, JobParameters jobParameters) {
        JobExecution jobExecution = new JobExecution(jobInstance, jobParameters);
        jobExecution.setEndTime(new Date());
        jobExecution.setStartTime(new Date());
        return jobExecution;
    }

    private Date getDateOfDay(int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }

    private JobParameters createJobParameters(long year, long month) {
        return new JobParametersBuilder()
                .addLong(JobStartParams.YEAR, year)
                .addLong(JobStartParams.MONTH, month)
                .toJobParameters();
    }
}