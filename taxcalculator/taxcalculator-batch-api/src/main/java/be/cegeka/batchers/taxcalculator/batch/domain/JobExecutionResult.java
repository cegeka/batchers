package be.cegeka.batchers.taxcalculator.batch.domain;

import java.util.Date;

public class JobExecutionResult {

    private Long id;
    private String status;
    private Date startTime;
    private Date endTime;
    private String description;


    public JobExecutionResult(Long id, String status, Date startTime, Date endTime, String description) {
        this.id = id;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
    }

    public Long getId() {
        return id;
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

    public String getDescription() {
        return description;
    }


    public Long getDuration() {
        if (endTime == null) {
            return null;
        }
        return endTime.getTime() - startTime.getTime();
    }
}
