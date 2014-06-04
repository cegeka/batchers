package be.cegeka.batchers.taxcalculator.batch.config.listeners;

import be.cegeka.batchers.taxcalculator.batch.api.events.JobProgressEvent;
import be.cegeka.batchers.taxcalculator.batch.api.events.JobStartRequest;
import com.google.common.eventbus.EventBus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@StepScope
public class JobProgressListener implements StepExecutionListener, ItemWriteListener {

    public static final int UPDATE_INTERVAL = 5;

    private int lastPercentageComplete = 0;
    private int currentItemCount;
    private int totalItemCount;
    private JobStartRequest jobStartRequest;
    private String stepName;
    @Autowired
    private EventBus eventBus;

    @PostConstruct
    public void setInitialTotalSkipLimit() {
        currentItemCount = 0;
        totalItemCount = 100;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        jobStartRequest = new JobStartRequest(stepExecution.getJobExecution().getJobConfigurationName(), 2014, 5);
        stepName = stepExecution.getStepName();
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
        if (percentageComplete - lastPercentageComplete > UPDATE_INTERVAL) {
            lastPercentageComplete = percentageComplete;
            eventBus.post(new JobProgressEvent(jobStartRequest, stepName, percentageComplete));
        }
    }

    @Override
    public void onWriteError(Exception exception, List items) {

    }
}
