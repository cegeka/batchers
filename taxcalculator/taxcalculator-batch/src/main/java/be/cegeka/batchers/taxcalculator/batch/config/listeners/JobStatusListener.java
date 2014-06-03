package be.cegeka.batchers.taxcalculator.batch.config.listeners;

import be.cegeka.batchers.taxcalculator.batch.api.events.JobEvent;
import be.cegeka.batchers.taxcalculator.batch.api.events.JobStartRequest;
import be.cegeka.batchers.taxcalculator.batch.mapping.JobStartRequestMapper;
import com.google.common.eventbus.EventBus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobStatusListener extends JobExecutionListenerSupport {

    @Autowired
    private EventBus eventBus;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        JobStartRequest jobStartRequest = new JobStartRequestMapper().map(jobExecution.getJobInstance().getJobName(), jobExecution.getJobParameters());

        eventBus.post(new JobEvent(jobStartRequest, jobExecution.getStatus().name()));
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        JobStartRequest jobStartRequest = new JobStartRequestMapper().map(jobExecution.getJobInstance().getJobName(), jobExecution.getJobParameters());

        eventBus.post(new JobEvent(jobStartRequest, jobExecution.getStatus().name()));
    }

}
