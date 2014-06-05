package be.cegeka.batchers.taxcalculator.batch.api.events;

import be.cegeka.batchers.taxcalculator.batch.domain.JobStartParams;

public class JobEvent {

    private JobStartParams jobStartParams;
    private String status;

    public JobEvent(JobStartParams jobStartRequest, String status) {
        this.jobStartParams = jobStartRequest;
        this.status = status;
    }

    public JobStartParams getJobStartParams() {
        return jobStartParams;
    }

    public String getStatus() {
        return status;
    }
}
