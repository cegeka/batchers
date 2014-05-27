package be.cegeka.batchers.taxcalculator.batch.domain;

import java.util.List;

public class JobResult {

    private String jobName;
    private JobStartParams jobStartParams;
    private List<JobExecutionResult> jobExecutionResults;

    public JobResult(String jobName, JobStartParams jobStartParams, List<JobExecutionResult> jobExecutionResults) {
        this.jobName = jobName;
        this.jobStartParams = jobStartParams;
        this.jobExecutionResults = jobExecutionResults;
    }

    public String getJobName() {
        return jobName;
    }

    public JobStartParams getJobStartParams() {
        return jobStartParams;
    }

    public List<JobExecutionResult> getJobExecutionResults() {
        return jobExecutionResults;
    }
}
