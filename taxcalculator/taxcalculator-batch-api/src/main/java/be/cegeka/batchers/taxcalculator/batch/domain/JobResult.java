package be.cegeka.batchers.taxcalculator.batch.domain;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import static java.util.Comparator.comparing;

public class JobResult {

    private String jobName;
    private JobStartParams jobStartParams;
    private List<JobExecutionResult> jobExecutionResults;
    private Function<JobExecutionResult, Date> onDateTime = jobResult -> jobResult.getEndTime();

    public JobResult(String jobName, JobStartParams jobStartParams, List<JobExecutionResult> jobExecutionResults) {
        this.jobName = jobName;
        this.jobStartParams = jobStartParams;
        this.jobExecutionResults = jobExecutionResults;
        Collections.sort(this.jobExecutionResults, comparing(onDateTime).reversed());
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
