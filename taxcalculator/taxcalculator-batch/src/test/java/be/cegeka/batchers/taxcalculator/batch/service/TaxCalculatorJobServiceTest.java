package be.cegeka.batchers.taxcalculator.batch.service;

import be.cegeka.batchers.taxcalculator.batch.api.JobStartListener;
import org.fest.assertions.api.Assertions;
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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
                .addLong("month", new Long(5), false)
                .addLong("year", new Long(2014), false).toJobParameters();

        doReturn(jobParameters).when(taxCalculatorJobService).getNewJobParameters();

        taxCalculatorJobService.startJobs();

        verify(jobLauncherMock).run(any(Job.class), eq(jobParameters));
    }

    @Test
    public void givenJob_whenGetNewJobParameters_thenParametersContainIdentifierMonthAndYear() {
        JobParameters jobParameters = taxCalculatorJobService.getNewJobParameters();

        assertThat(jobParameters.getParameters()).hasSize(3);
        assertThat(jobParameters.getLong("uniqueIdentifier")).isNotNull();
        assertThat(jobParameters.getParameters().get("uniqueIdentifier").getValue()).isNotNull();
        assertThat(jobParameters.getParameters().get("uniqueIdentifier").isIdentifying()).isTrue();

        assertThat((Long)jobParameters.getParameters().get("month").getValue()).isEqualTo(Calendar.getInstance().get(Calendar.MONTH));
        assertThat(jobParameters.getParameters().get("month").isIdentifying()).isFalse();

        assertThat((Long)jobParameters.getParameters().get("year").getValue()).isEqualTo(Calendar.getInstance().get(Calendar.YEAR));
        assertThat(jobParameters.getParameters().get("year").isIdentifying()).isFalse();
    }
}
