package be.cegeka.batchers.taxcalculator.batch.api.events;

public class JobProgressEvent {

    private JobStartRequest jobStartRequest;
    private String stepName;
    private int percentageComplete;

    public JobProgressEvent(JobStartRequest jobStartRequest, String stepName, int percentageComplete) {
        this.jobStartRequest = jobStartRequest;
        this.stepName = stepName;
        this.percentageComplete = percentageComplete;
    }

    public JobStartRequest getJobStartRequest() {
        return jobStartRequest;
    }

    public String getStepName() {
        return stepName;
    }

    public int getPercentageComplete() {
        return percentageComplete;
    }
}
