package be.cegeka.batchers.taxcalculator.batch.service;

import be.cegeka.batchers.taxcalculator.batch.api.JobStartListener;
import be.cegeka.batchers.taxcalculator.batch.api.events.JobStartRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

import java.util.Date;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

@RunWith(MockitoJUnitRunner.class)
public class TaxCalculatorJobServiceTest {
    public static final String A_JOBS_NAME = "Steve Jobs :-)";
    public static final Long YEAR = 2004L;
    public static final Long MONTH = 2L;

    @InjectMocks
    private TaxCalculatorJobService taxCalculatorJobService;

    @Mock
    private JobStartListener jobStartListenerMock1;
    @Mock
    private JobStartListener jobStartListenerMock2;
    @Mock
    private Job jobMock;
    @Mock
    private SimpleJobLauncher jobLauncherMock;
    @Mock
    private JobExecution jobExecution;
    @Mock
    private Date currentDate;
    @Captor
    private ArgumentCaptor<JobParameters> jobParametersArgumentCaptor;

    @Before
    public void setUpJobLauncher() throws Exception {
        setInternalState(taxCalculatorJobService, "jobStartListeners",
                asList(jobStartListenerMock1, jobStartListenerMock2).stream().collect(toSet())
        );

        when(jobLauncherMock.run(any(Job.class), any(JobParameters.class))).thenReturn(jobExecution);
    }

    @Test
    public void onJobStarted_AllJobStartListenersAreNotified() {
        when(jobMock.getName()).thenReturn(A_JOBS_NAME);

        taxCalculatorJobService.runTaxCalculatorJob(new JobStartRequest(null, YEAR.intValue(), MONTH.intValue()));

        verify(jobStartListenerMock1).jobHasBeenStarted(A_JOBS_NAME);
        verify(jobStartListenerMock2).jobHasBeenStarted(A_JOBS_NAME);
    }

    @Test
    public void whenStarJobs_withGivenYearAndMonth_runJobWithParameters() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        taxCalculatorJobService.runTaxCalculatorJob(new JobStartRequest(null, YEAR.intValue(), MONTH.intValue()));

        verify(jobLauncherMock).run(any(Job.class), jobParametersArgumentCaptor.capture());

        JobParameters jobParameters = jobParametersArgumentCaptor.getValue();
        assertThat(jobParameters.getLong("year")).isEqualTo(YEAR);
        assertThat(jobParameters.getLong("month")).isEqualTo(MONTH);
    }
}
