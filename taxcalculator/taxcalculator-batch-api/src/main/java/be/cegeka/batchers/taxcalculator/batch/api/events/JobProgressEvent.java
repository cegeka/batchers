package be.cegeka.batchers.taxcalculator.batch.api.events;

import be.cegeka.batchers.taxcalculator.batch.domain.JobStartParams;

import java.io.Serializable;

public class JobProgressEvent implements JobEvent {

    private JobStartParams jobStartParams;
    private String stepName;
    private int percentageComplete;

    public JobProgressEvent(JobStartParams jobStartParams, String stepName, int percentageComplete) {
        this.jobStartParams = jobStartParams;
        this.stepName = stepName;
        this.percentageComplete = percentageComplete;
    }

    public JobStartParams getJobStartParams() {
        return jobStartParams;
    }

    public String getStepName() {
        return stepName;
    }

    public int getPercentageComplete() {
        return percentageComplete;
    }
}
