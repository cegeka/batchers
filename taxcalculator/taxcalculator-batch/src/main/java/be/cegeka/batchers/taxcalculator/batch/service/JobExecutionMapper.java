package be.cegeka.batchers.taxcalculator.batch.service;

import be.cegeka.batchers.taxcalculator.batch.domain.JobExecutionResult;
import be.cegeka.batchers.taxcalculator.batch.domain.JobStartParams;
import org.springframework.batch.core.JobParameters;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class JobExecutionMapper {

    public List<JobExecutionResult> getJobExecutionResults(List<org.springframework.batch.core.JobExecution> jobExecutions) {
        return jobExecutions
                .stream()
                .map(jobExec -> new JobExecutionResult(
                        jobExec.getId(), jobExec.getStatus().toString(),
                        jobExec.getStartTime(), jobExec.getEndTime(),
                        getDescription(jobExec), getJobStartParams(jobExec.getJobParameters())))
                .collect(Collectors.toList());
    }

    private String getDescription(org.springframework.batch.core.JobExecution jobExec) {
        return jobExec.getJobParameters().getLong(JobStartParams.MONTH) + "/" + jobExec.getJobParameters().getLong(JobStartParams.YEAR)
                + ", id :" + jobExec.getId();
    }

    private JobStartParams getJobStartParams(JobParameters jobParameters) {
        return new JobStartParams(jobParameters.getLong(JobStartParams.YEAR), jobParameters.getLong(JobStartParams.MONTH));
    }
}
