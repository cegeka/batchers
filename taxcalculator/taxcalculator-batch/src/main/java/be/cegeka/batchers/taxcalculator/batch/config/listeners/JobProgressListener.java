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

@Component
@StepScope
public class JobProgressListener implements StepExecutionListener, ItemWriteListener {

    private int lastPercentageComplete = 0;
    private int currentItemCount;
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
        currentItemCount = 0;
        lastPercentageComplete = 0;
        eventBus.post(new JobProgressEvent(jobStartParams, stepName, lastPercentageComplete));
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
        currentItemCount += items.size();

        int percentageComplete = currentItemCount * 100 / totalItemCount;
        if (percentageComplete > lastPercentageComplete) {
            lastPercentageComplete = percentageComplete;
            eventBus.post(new JobProgressEvent(jobStartParams, stepName, percentageComplete));
        }
    }

    @Override
    public void onWriteError(Exception exception, List items) {

    }
}
