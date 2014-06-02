package be.cegeka.batchers.taxcalculator.batch.api.events;

public class JobEvent {

    private JobStartRequest jobStartRequest;
    private String status;

    public JobEvent(JobStartRequest jobStartRequest, String status) {
        this.jobStartRequest = jobStartRequest;
        this.status = status;
    }

    public JobStartRequest getJobStartRequest() {
        return jobStartRequest;
    }

    public String getStatus() {
        return status;
    }
}
