package be.cegeka.batchers.taxcalculator.batch.config.listeners;

import be.cegeka.batchers.taxcalculator.batch.api.events.JobEvent;
import be.cegeka.batchers.taxcalculator.batch.domain.JobStartParams;
import be.cegeka.batchers.taxcalculator.batch.mapping.JobStartParamsMapper;
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
        JobStartParams jobStartParams = new JobStartParamsMapper().map(jobExecution.getJobParameters());

        eventBus.post(new JobEvent(jobStartParams, jobExecution.getStatus().name()));
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        JobStartParams jobStartParams = new JobStartParamsMapper().map(jobExecution.getJobParameters());

        eventBus.post(new JobEvent(jobStartParams, jobExecution.getStatus().name()));
    }

}
