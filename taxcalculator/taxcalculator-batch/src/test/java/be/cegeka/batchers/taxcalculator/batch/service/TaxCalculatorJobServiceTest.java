package be.cegeka.batchers.taxcalculator.batch.service;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TaxCalculatorJobServiceTest {

    @InjectMocks
    TaxCalculatorJobService taxCalculatorJobService;

    @Mock
    SimpleJobLauncher jobLauncher;
    @Mock
    Job employeeJob;

    @Test
    public void whenTaxJobServiceIsTriggered_thenJobExecutorIsCalled() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        taxCalculatorJobService.runTaxCalculatorJob();
        verify(jobLauncher).run(eq(employeeJob), any(JobParameters.class)) ;
    }
}
