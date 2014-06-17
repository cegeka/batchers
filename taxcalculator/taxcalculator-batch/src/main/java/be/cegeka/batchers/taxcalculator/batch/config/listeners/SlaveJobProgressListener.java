package be.cegeka.batchers.taxcalculator.batch.config.listeners;

import be.cegeka.batchers.taxcalculator.batch.api.events.JobProgressEvent;
import be.cegeka.batchers.taxcalculator.batch.config.remotepartitioning.ClusterConfig;
import be.cegeka.batchers.taxcalculator.batch.domain.JobStartParams;
import be.cegeka.batchers.taxcalculator.batch.mapping.JobStartParamsMapper;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.AfterWrite;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile(value = {"remotePartitioningSlave", "remotePartitioningTest"})
public class SlaveJobProgressListener implements JobProgressListener {

    @Autowired
    private ClusterConfig clusterConfig;
    private JobStartParams jobStartParams;
    private String stepName;

    @Override
    public void beforeWrite(List items) {

    }

    @Override
    @AfterWrite
    public void afterWrite(List items) {
        int itemsDone = items.size();
        JobProgressEvent jobProgressEvent = new JobProgressEvent(jobStartParams, stepName, itemsDone);
        clusterConfig.jobProgressEventsTopic().publish(jobProgressEvent);
    }

    @Override
    public void onWriteError(Exception exception, List items) {
    }

    @Override
    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        jobStartParams = new JobStartParamsMapper().map(stepExecution.getJobParameters());
        stepName = stepExecution.getStepName();
    }

    @Override
    @AfterStep
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }
}
