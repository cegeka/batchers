package be.cegeka.batchers.taxcalculator.batch.mapping;

import be.cegeka.batchers.taxcalculator.batch.api.events.JobStartRequest;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;

public class JobStartRequestMapper {

    public JobStartRequest map(String jobName, JobParameters jobParameters) {
        return new JobStartRequest(jobName, jobParameters.getLong("year").intValue(), jobParameters.getLong("month").intValue());
    }
}
