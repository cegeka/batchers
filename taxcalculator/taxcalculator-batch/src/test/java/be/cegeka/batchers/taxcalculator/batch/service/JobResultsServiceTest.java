package be.cegeka.batchers.taxcalculator.batch.service;


import be.cegeka.batchers.taxcalculator.batch.config.EmployeeJobConfig;
import be.cegeka.batchers.taxcalculator.batch.domain.JobExecutionResult;
import be.cegeka.batchers.taxcalculator.batch.domain.JobStartParams;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
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

        org.springframework.batch.core.JobExecution jobInstance1_jobExecution1 = createJobExecution(jobInstance1, createJobParameters(2014, 5));
        when(jobExplorer.getJobExecutions(jobInstance1)).thenReturn(asList(jobInstance1_jobExecution1));

        org.springframework.batch.core.JobExecution jobInstance2_jobExecution1 = createJobExecution(jobInstance2, createJobParameters(2013, 6));
        org.springframework.batch.core.JobExecution jobInstance2_jobExecution2 = createJobExecution(jobInstance2, createJobParameters(2013, 6));
        when(jobExplorer.getJobExecutions(jobInstance2)).thenReturn(asList(jobInstance2_jobExecution1, jobInstance2_jobExecution2));

        //ACT
        List<JobExecutionResult> jobResults = jobResultsService.getJobResults();

        //ASSERT
        verify(jobExplorer).getJobInstancesByJobName(EmployeeJobConfig.EMPLOYEE_JOB, 0, MAX_VALUE);
        verify(jobExplorer).getJobExecutions(jobInstance1);
        verify(jobExplorer).getJobExecutions(jobInstance2);

        assertThat(jobResults).hasSize(6);
    }

    @Test
    public void testGetFinishedJobResults_SameDates_SortingIsDescOnDate() throws Exception {
        //ARRANGE
        JobInstance jobInstance1 = new JobInstance(1L, EmployeeJobConfig.EMPLOYEE_JOB);

        when(jobExplorer.getJobInstancesByJobName(EmployeeJobConfig.EMPLOYEE_JOB, 0, MAX_VALUE))
                .thenReturn(asList(jobInstance1));

        org.springframework.batch.core.JobExecution jobInstance1_jobExecution1 = new org.springframework.batch.core.JobExecution(jobInstance1, 1L, createJobParameters(2014, 6), null);
        jobInstance1_jobExecution1.setEndTime(getDateOfDay(3));
        org.springframework.batch.core.JobExecution jobInstance1_jobExecution2 = new org.springframework.batch.core.JobExecution(jobInstance1, 2L, createJobParameters(2014, 6), null);
        jobInstance1_jobExecution2.setEndTime(getDateOfDay(4));

        when(jobExplorer.getJobExecutions(jobInstance1)).thenReturn(asList(jobInstance1_jobExecution1, jobInstance1_jobExecution2));
        //ACT
        List<JobExecutionResult> jobResults = jobResultsService.getJobResults();

        //TODO
        //assertThat(jobResults.get(5).getJobExecutionResults().get(0).getEndTime()).isAfter(jobResults.get(5).getJobExecutionResults().get(1).getEndTime());
    }

    @Test
    public void testGetJobResults_Returns_First6Months() {
        List<JobExecutionResult> jobResults = jobResultsService.getJobResults();
        //TODO
//        assertThat(jobResults).hasSize(6);
//        assertThat(jobResults.get(0).getMonth()).isEqualTo(1L);
//        assertThat(jobResults.get(1).getMonth()).isEqualTo(2L);
//        assertThat(jobResults.get(2).getMonth()).isEqualTo(3L);
//        assertThat(jobResults.get(3).getMonth()).isEqualTo(4L);
//        assertThat(jobResults.get(4).getMonth()).isEqualTo(5L);
//        assertThat(jobResults.get(5).getMonth()).isEqualTo(6L);
    }

    private org.springframework.batch.core.JobExecution createJobExecution(JobInstance jobInstance, JobParameters jobParameters) {
        org.springframework.batch.core.JobExecution jobExecution = new org.springframework.batch.core.JobExecution(jobInstance, jobParameters);
        jobExecution.setId(1L);
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