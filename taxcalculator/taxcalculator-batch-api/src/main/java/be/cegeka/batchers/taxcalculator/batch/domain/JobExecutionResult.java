package be.cegeka.batchers.taxcalculator.batch.domain;

import java.util.Date;

public class JobExecutionResult {

    private String status;
    private Date dateTime;
    private String executionId;

    public JobExecutionResult(String status, Date dateTime, String executionId) {
        this.status = status;
        this.dateTime = dateTime;
        this.executionId = executionId;
    }

    public String getStatus() {
        return status;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public String getExecutionId() {
        return executionId;
    }


}
