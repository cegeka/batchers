package be.cegeka.batchers.taxcalculator.batch.service;


import be.cegeka.batchers.taxcalculator.batch.config.singlejvm.EmployeeJobConfigSingleJvm;
import be.cegeka.batchers.taxcalculator.batch.domain.JobResult;
import be.cegeka.batchers.taxcalculator.batch.domain.JobStartParams;
import org.joda.time.DateTime;
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
public class JobResultsServiceImplTest {
    @InjectMocks
    JobResultsServiceImpl jobResultsService;

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
        JobInstance jobInstance1 = new JobInstance(1L, EmployeeJobConfigSingleJvm.EMPLOYEE_JOB);
        JobInstance jobInstance2 = new JobInstance(2L, EmployeeJobConfigSingleJvm.EMPLOYEE_JOB);
        List<JobInstance> jobInstances = asList(jobInstance1, jobInstance2);

        when(jobExplorer.getJobInstancesByJobName(EmployeeJobConfigSingleJvm.EMPLOYEE_JOB, 0, MAX_VALUE))
                .thenReturn(jobInstances);

        JobExecution jobInstance1_jobExecution1 = createJobExecution(jobInstance1, createJobParameters(2014, 5));
        when(jobExplorer.getJobExecutions(jobInstance1)).thenReturn(asList(jobInstance1_jobExecution1));

        JobExecution jobInstance2_jobExecution1 = createJobExecution(jobInstance2, createJobParameters(2013, 6));
        JobExecution jobInstance2_jobExecution2 = createJobExecution(jobInstance2, createJobParameters(2013, 6));
        when(jobExplorer.getJobExecutions(jobInstance2)).thenReturn(asList(jobInstance2_jobExecution1, jobInstance2_jobExecution2));

        //ACT
        List<JobResult> jobResults = jobResultsService.getJobResults();

        //ASSERT
        verify(jobExplorer).getJobInstancesByJobName(EmployeeJobConfigSingleJvm.EMPLOYEE_JOB, 0, MAX_VALUE);
        verify(jobExplorer).getJobExecutions(jobInstance1);
        verify(jobExplorer).getJobExecutions(jobInstance2);

        assertThat(jobResults).hasSize(6);
    }

    @Test
    public void testGetFinishedJobResults_SameDates_SortingIsDescOnDate() throws Exception {
        //ARRANGE
        JobInstance jobInstance1 = new JobInstance(1L, EmployeeJobConfigSingleJvm.EMPLOYEE_JOB);

        when(jobExplorer.getJobInstancesByJobName(EmployeeJobConfigSingleJvm.EMPLOYEE_JOB, 0, MAX_VALUE))
                .thenReturn(asList(jobInstance1));

        DateTime dateTime = new DateTime();
        JobExecution jobInstance1_jobExecution1 = new JobExecution(jobInstance1, 1L, createJobParameters(dateTime.getYear(), dateTime.getMonthOfYear()), null);
        jobInstance1_jobExecution1.setEndTime(getDateOfDay(3));
        JobExecution jobInstance1_jobExecution2 = new JobExecution(jobInstance1, 2L, createJobParameters(dateTime.getYear(), dateTime.getMonthOfYear()), null);
        jobInstance1_jobExecution2.setEndTime(getDateOfDay(4));

        when(jobExplorer.getJobExecutions(jobInstance1)).thenReturn(asList(jobInstance1_jobExecution1, jobInstance1_jobExecution2));
        //ACT
        List<JobResult> jobResults = jobResultsService.getJobResults();

        assertThat(jobResults.get(0).getJobExecutionResults().get(0).getEndTime()).isAfter(jobResults.get(0).getJobExecutionResults().get(1).getEndTime());
    }

    @Test
    public void testGetJobResults_Returns_Last6Months() {
        List<JobResult> jobResults = jobResultsService.getJobResults();

        assertThat(jobResults).hasSize(6);
    }

    @Test
    public void testGetJobStartParamsPreviousMonths() {
        int month = 3;
        int year = 2014;

        List<JobStartParams> jobStartParamsPreviousMonths = jobResultsService.getJobStartParamsPreviousMonths(year, month);
        assertThat(jobStartParamsPreviousMonths.size()).isEqualTo(6);
        assertThat(jobStartParamsPreviousMonths.get(0).getMonth()).isEqualTo(month);
        assertThat(jobStartParamsPreviousMonths.get(0).getYear()).isEqualTo(year);

        assertThat(jobStartParamsPreviousMonths.get(1).getMonth()).isEqualTo(month - 1);
        assertThat(jobStartParamsPreviousMonths.get(1).getYear()).isEqualTo(year);

        assertThat(jobStartParamsPreviousMonths.get(2).getMonth()).isEqualTo(month - 2);
        assertThat(jobStartParamsPreviousMonths.get(2).getYear()).isEqualTo(year);

        assertThat(jobStartParamsPreviousMonths.get(3).getMonth()).isEqualTo(12);
        assertThat(jobStartParamsPreviousMonths.get(3).getYear()).isEqualTo(year - 1);

        assertThat(jobStartParamsPreviousMonths.get(4).getMonth()).isEqualTo(11);
        assertThat(jobStartParamsPreviousMonths.get(4).getYear()).isEqualTo(year - 1);

        assertThat(jobStartParamsPreviousMonths.get(5).getMonth()).isEqualTo(10);
        assertThat(jobStartParamsPreviousMonths.get(5).getYear()).isEqualTo(year - 1);
    }

    private JobExecution createJobExecution(JobInstance jobInstance, JobParameters jobParameters) {
        JobExecution jobExecution = new JobExecution(jobInstance, jobParameters);
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