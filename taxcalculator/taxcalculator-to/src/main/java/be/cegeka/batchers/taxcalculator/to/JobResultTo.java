package be.cegeka.batchers.taxcalculator.to;

import java.util.Date;

public class JobResultTo {
    private String status;
    private Date dateTime;
    private String executionId;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }
}
