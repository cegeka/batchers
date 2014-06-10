package be.cegeka.batchers.taxcalculator.batch.mapping;

import be.cegeka.batchers.taxcalculator.batch.domain.JobStartParams;
import org.springframework.batch.core.JobParameters;

public class JobStartParamsMapper {

    public JobStartParams map(JobParameters jobParameters) {
        return new JobStartParams(jobParameters.getLong("year").intValue(), jobParameters.getLong("month").intValue());
    }
}
