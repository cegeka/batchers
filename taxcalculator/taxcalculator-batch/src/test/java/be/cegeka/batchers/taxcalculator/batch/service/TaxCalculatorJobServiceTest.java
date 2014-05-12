package be.cegeka.batchers.taxcalculator.batch.service;

import be.cegeka.batchers.taxcalculator.batch.api.JobStartListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TaxCalculatorJobServiceTest {
    public static final String A_JOBS_NAME = "Steve Jobs :-)";
    @InjectMocks
    TaxCalculatorJobService taxCalculatorJobService;

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

    @Before
    public void setUpJobLauncher() throws Exception {
        Whitebox.setInternalState(taxCalculatorJobService, "jobStartListeners",
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
}
