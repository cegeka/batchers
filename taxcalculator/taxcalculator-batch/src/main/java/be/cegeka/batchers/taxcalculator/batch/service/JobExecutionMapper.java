package be.cegeka.batchers.taxcalculator.batch.service;

import be.cegeka.batchers.taxcalculator.to.JobResultTo;
import org.springframework.batch.core.JobExecution;

public class JobExecutionMapper {
    public JobResultTo toJobResultTo(JobExecution jobExecution) {
        JobResultTo jobResultTo = new JobResultTo();
        jobResultTo.setStatus(jobExecution.getStatus().name());
        jobResultTo.setDateTime(jobExecution.getCreateTime());
        jobResultTo.setExecutionId(jobExecution.getJobInstance().getJobName() + " " + jobExecution.getId());

        return jobResultTo;
    }
}
