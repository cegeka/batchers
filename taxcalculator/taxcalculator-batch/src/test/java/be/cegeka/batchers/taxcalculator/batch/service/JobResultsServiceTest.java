package be.cegeka.batchers.taxcalculator.batch.service;


import be.cegeka.batchers.taxcalculator.batch.config.EmployeeJobConfig;
import be.cegeka.batchers.taxcalculator.to.JobResultTo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;

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
    @Mock
    JobExecutionMapper mapperMock;

    @Test
    public void testName() throws Exception {
        //ARRANGE
        JobInstance jobInstance1 = new JobInstance(1L, EmployeeJobConfig.EMPLOYEE_JOB);
        JobInstance jobInstance2 = new JobInstance(2L, EmployeeJobConfig.EMPLOYEE_JOB);
        List<JobInstance> jobInstances = asList(jobInstance1, jobInstance2);

        when(jobExplorer.getJobInstancesByJobName(EmployeeJobConfig.EMPLOYEE_JOB, 0, MAX_VALUE))
                .thenReturn(jobInstances);

        JobExecution jobInstance1_jobExecution1 = new JobExecution(1L);
        when(jobExplorer.getJobExecutions(jobInstance1)).thenReturn(asList(jobInstance1_jobExecution1));

        JobExecution jobInstance2_jobExecution1 = new JobExecution(2L);
        JobExecution jobInstance2_jobExecution2 = new JobExecution(3L);
        when(jobExplorer.getJobExecutions(jobInstance2)).thenReturn(asList(jobInstance2_jobExecution1, jobInstance2_jobExecution2));

        JobResultTo jobResultTo1 = new JobResultTo();
        JobResultTo jobResultTo2 = new JobResultTo();
        JobResultTo jobResultTo3 = new JobResultTo();

        when(mapperMock.toJobResultTo(jobInstance1_jobExecution1)).thenReturn(jobResultTo1);
        when(mapperMock.toJobResultTo(jobInstance2_jobExecution1)).thenReturn(jobResultTo2);
        when(mapperMock.toJobResultTo(jobInstance2_jobExecution2)).thenReturn(jobResultTo3);

        //ACT
        List<JobResultTo> jobResults = jobResultsService.getFinishedJobResults();

        //ASSERT
        verify(jobExplorer).getJobInstancesByJobName(EmployeeJobConfig.EMPLOYEE_JOB, 0, MAX_VALUE);
        verify(jobExplorer).getJobExecutions(jobInstance1);
        verify(jobExplorer).getJobExecutions(jobInstance2);

        assertThat(jobResults).containsOnly(jobResultTo1, jobResultTo2, jobResultTo3);
    }
}