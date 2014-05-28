package be.cegeka.batchers.taxcalculator.batch.service;

import be.cegeka.batchers.taxcalculator.batch.api.JobStartListener;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

import java.util.Date;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

@RunWith(MockitoJUnitRunner.class)
public class TaxCalculatorJobServiceTest {
    public static final String A_JOBS_NAME = "Steve Jobs :-)";

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

        taxCalculatorJobService.runTaxCalculatorJob();

        verify(jobStartListenerMock1).jobHasBeenStarted(A_JOBS_NAME);
        verify(jobStartListenerMock2).jobHasBeenStarted(A_JOBS_NAME);
    }

    @Test
    public void givenJob_whenCalculatingParameters_thenAUniqueIdentifierIsUsed() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        taxCalculatorJobService = spy(taxCalculatorJobService);
        JobParameters jobParameters = new JobParametersBuilder().addLong("uniqueIdentifier", new Date().getTime())
                .addLong("month", 5L, false)
                .addLong("year", 2014L, false).toJobParameters();

        doReturn(jobParameters).when(taxCalculatorJobService).getNewJobParameters();

        taxCalculatorJobService.startJobs();

        verify(jobLauncherMock).run(any(Job.class), eq(jobParameters));
    }

    @Test
    public void givenJob_whenGetNewJobParameters_thenParametersContainMonthAndYear() {
        JobParameters jobParameters = taxCalculatorJobService.getNewJobParameters();

        assertThat(jobParameters.getParameters()).hasSize(2);

        assertThat((Long) jobParameters.getParameters().get("month").getValue()).isEqualTo(new DateTime().getMonthOfYear());
        assertThat(jobParameters.getParameters().get("month").isIdentifying()).isTrue();

        assertThat((Long) jobParameters.getParameters().get("year").getValue()).isEqualTo(new DateTime().getYear());
        assertThat(jobParameters.getParameters().get("year").isIdentifying()).isTrue();
    }
}
