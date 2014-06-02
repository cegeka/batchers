package be.cegeka.batchers.taxcalculator.batch.service;

import be.cegeka.batchers.taxcalculator.batch.domain.JobExecutionResult;
import be.cegeka.batchers.taxcalculator.batch.domain.JobResult;
import be.cegeka.batchers.taxcalculator.batch.domain.JobStartParams;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JobExecutionMapper {

    public JobResult toJobResultTo(Map.Entry<JobInstance, List<JobExecution>> mapEntry) {
        JobInstance jobInstance = mapEntry.getKey();
        List<JobExecution> jobExecutions = mapEntry.getValue();

        JobStartParams jobStartParams = getJobStartParams(jobExecutions.get(0).getJobParameters());
        List<JobExecutionResult> jobExecutionResults = getJobExecutionResults(jobExecutions);

        JobResult jobResult = new JobResult(getJobName(jobInstance, jobStartParams), jobStartParams, jobExecutionResults);
        return jobResult;
    }

    private String getJobName(JobInstance jobInstance, JobStartParams jobStartParams) {
        return jobInstance.getJobName() + " id: " + jobInstance.getId();
    }

    private List<JobExecutionResult> getJobExecutionResults(List<JobExecution> jobExecutions) {
        return jobExecutions
                .stream()
                .map(jobExec -> new JobExecutionResult(
                        jobExec.getId(), jobExec.getStatus().toString(),
                        jobExec.getStartTime(), jobExec.getEndTime(),
                        getDescription(jobExec)))
                .collect(Collectors.toList());
    }

    private String getDescription(JobExecution jobExec) {
        return jobExec.getJobParameters().getLong(JobStartParams.MONTH) + "/" + jobExec.getJobParameters().getLong(JobStartParams.YEAR)
                + ", id :" + jobExec.getId();
    }

    private JobStartParams getJobStartParams(JobParameters jobParameters) {
        return new JobStartParams(jobParameters.getLong(JobStartParams.YEAR), jobParameters.getLong(JobStartParams.MONTH));
    }
}
