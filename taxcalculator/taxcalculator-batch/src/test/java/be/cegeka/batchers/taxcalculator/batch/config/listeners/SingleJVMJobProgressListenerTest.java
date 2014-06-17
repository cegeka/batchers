package be.cegeka.batchers.taxcalculator.batch.config.listeners;

import be.cegeka.batchers.taxcalculator.application.service.EmployeeService;
import be.cegeka.batchers.taxcalculator.batch.api.events.JobProgressEvent;
import com.google.common.eventbus.EventBus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static be.cegeka.batchers.taxcalculator.application.ApplicationAssertions.assertThat;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class SingleJVMJobProgressListenerTest {

    public static final long YEAR = 2000L;
    public static final long MONTH = 5L;
    public static final String STEP_NAME = "Step name";

    @InjectMocks
    private SingleJVMJobProgressListener jobProgressListener;

    @Mock
    private EventBus eventBus;

    @Mock
    private EmployeeService employeeService;

    private ArgumentCaptor<JobProgressEvent> eventArgumentCaptor = ArgumentCaptor.forClass(JobProgressEvent.class);

    @Test
    public void givenAStepIsStarted_when_thenAnEventIsTriggeredWithZeroProgress() {
        startAStepWithEmployees(1);
        Mockito.verify(eventBus).post(eventArgumentCaptor.capture());
        checkPercentageComplete(eventArgumentCaptor.getValue(), 0);
    }

    @Test
    public void givenAStepStartedWith1000Employees_whenWritingOneItem_thenNoNewEventIsTriggered() {
        startAStepWithEmployees(1000);
        jobProgressListener.afterWrite(aListWithSize(1));

        Mockito.verify(eventBus, times(1)).post(eventArgumentCaptor.capture());
        JobProgressEvent jobProgressEvent = eventArgumentCaptor.getValue();

        checkPercentageComplete(jobProgressEvent, 0);
    }

    @Test
    public void givenAStepStartedWith100Employees_whenWritingOneItem_thenAProgressEventIsTriggered() {
        startAStepWithEmployees(100);
        jobProgressListener.afterWrite(aListWithSize(1));

        Mockito.verify(eventBus, times(2)).post(eventArgumentCaptor.capture());
        JobProgressEvent jobProgressEvent = eventArgumentCaptor.getValue();

        checkPercentageComplete(jobProgressEvent, 1);
    }

    @Test
    public void givenAStepStartedWith1000Employees_when999ItemsAreWrittenTwice_thenProgressIs99() {
        startAStepWithEmployees(1000);

        jobProgressListener.afterWrite(aListWithSize(999));

        Mockito.verify(eventBus, times(2)).post(eventArgumentCaptor.capture());
        JobProgressEvent jobProgressEvent = eventArgumentCaptor.getValue();

        checkPercentageComplete(jobProgressEvent, 99);
    }

    @Test
    public void givenAStepStarted_whenWritingAllItemsAtOnce_thenAnEventWith100ProgressIsTriggered() {
        startAStepWithEmployees(100);
        jobProgressListener.afterWrite(aListWithSize(100));

        Mockito.verify(eventBus, times(2)).post(eventArgumentCaptor.capture());

        checkPercentageComplete(eventArgumentCaptor.getAllValues().get(0), 0);
        checkPercentageComplete(eventArgumentCaptor.getAllValues().get(1), 100);
    }

    @Test
    public void givenAStepStartedWith100Employees_when5ItemsAreWritten_thenAnEventIsTriggeredWithProgress() {
        startAStepWithEmployees(100);
        jobProgressListener.afterWrite(aListWithSize(25));
        jobProgressListener.afterWrite(aListWithSize(25));
        jobProgressListener.afterWrite(aListWithSize(25));
        jobProgressListener.afterWrite(aListWithSize(25));

        Mockito.verify(eventBus, times(5)).post(eventArgumentCaptor.capture());

        checkPercentageComplete(eventArgumentCaptor.getAllValues().get(0), 0);
        checkPercentageComplete(eventArgumentCaptor.getAllValues().get(1), 25);
        checkPercentageComplete(eventArgumentCaptor.getAllValues().get(2), 50);
        checkPercentageComplete(eventArgumentCaptor.getAllValues().get(3), 75);
        checkPercentageComplete(eventArgumentCaptor.getAllValues().get(4), 100);

    }

    public void startAStepWithEmployees(int size) {
        Mockito.when(employeeService.getEmployeeCount()).thenReturn((long) size);

        Map<String, JobParameter> parameters = new HashMap<>();
        parameters.put("year", new JobParameter(YEAR, true));
        parameters.put("month", new JobParameter(MONTH, true));

        jobProgressListener.beforeStep(new StepExecution(STEP_NAME, new JobExecution(1L, new JobParameters(parameters)), 1L));
    }

    private void checkPercentageComplete(JobProgressEvent jobProgressEvent, int expectedPercentage) {
        assertThat(jobProgressEvent.getStepName()).isEqualTo(STEP_NAME);
        assertThat(jobProgressEvent.getJobStartParams().getYear()).isEqualTo((int) YEAR);
        assertThat(jobProgressEvent.getJobStartParams().getMonth()).isEqualTo((int) MONTH);
        assertThat(jobProgressEvent.getPercentageComplete()).isEqualTo(expectedPercentage);
    }

    private List<Object> aListWithSize(int size) {
        return Collections.nCopies(size, new Object());
    }
}
