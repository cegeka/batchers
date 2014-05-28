package be.cegeka.batchers.taxcalculator.batch.domain;

import java.util.Date;

public class JobExecutionResult {

    private String status;
    private Date startTime;
    private Date endTime;
    private String executionId;


    public JobExecutionResult(String status, Date startTime, Date endTime, String executionId) {
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.executionId = executionId;
    }

    public String getStatus() {
        return status;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public String getExecutionId() {
        return executionId;
    }


    public Long getDuration() {
        if (endTime == null) {
            return null;
        }
        return endTime.getTime() - startTime.getTime();
    }
}
