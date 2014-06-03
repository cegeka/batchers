package be.cegeka.batchers.taxcalculator.batch.domain;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static java.util.Comparator.comparing;

public class JobResult {

    private String jobName;
    private JobStartParams jobStartParams;
    private List<JobExecutionResult> jobExecutionResults;
    private Function<JobExecutionResult, Long> onId = jobExecutionResult -> jobExecutionResult.getId();

    public JobResult(String jobName, JobStartParams jobStartParams, List<JobExecutionResult> jobExecutionResults) {
        this.jobName = jobName;
        this.jobStartParams = jobStartParams;
        this.jobExecutionResults = jobExecutionResults;
        Collections.sort(this.jobExecutionResults, comparing(onId).reversed());
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
