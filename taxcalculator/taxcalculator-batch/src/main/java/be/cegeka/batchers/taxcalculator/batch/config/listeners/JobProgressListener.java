package be.cegeka.batchers.taxcalculator.batch.config.listeners;

import be.cegeka.batchers.taxcalculator.application.service.EmployeeService;
import be.cegeka.batchers.taxcalculator.batch.api.events.JobProgressEvent;
import be.cegeka.batchers.taxcalculator.batch.domain.JobStartParams;
import be.cegeka.batchers.taxcalculator.batch.mapping.JobStartParamsMapper;
import com.google.common.eventbus.EventBus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Component
@StepScope
public class JobProgressListener implements StepExecutionListener, ItemWriteListener {

    private AtomicInteger lastPercentageComplete;
    private AtomicLong currentItemCount;
    private int totalItemCount;
    private JobStartParams jobStartParams;
    private String stepName;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EventBus eventBus;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        totalItemCount = employeeService.getEmployeeCount().intValue();
        jobStartParams = new JobStartParamsMapper().map(stepExecution.getJobParameters());
        stepName = stepExecution.getStepName();
        currentItemCount = new AtomicLong();
        lastPercentageComplete = new AtomicInteger();
        eventBus.post(new JobProgressEvent(jobStartParams, stepName, 0));
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }

    @Override
    public void beforeWrite(List items) {
    }

    @Override
    public void afterWrite(List items) {
        currentItemCount.addAndGet(items.size());
        int percentageComplete = currentItemCount.intValue() * 100 / totalItemCount;

        sentUpdateIfNeeded(percentageComplete);
    }

    private synchronized void sentUpdateIfNeeded(int percentageComplete) {
        if (percentageComplete > lastPercentageComplete.get()) {
            lastPercentageComplete = new AtomicInteger(percentageComplete);
            eventBus.post(new JobProgressEvent(jobStartParams, stepName, lastPercentageComplete.intValue()));
        }
    }

    @Override
    public void onWriteError(Exception exception, List items) {

    }
}
