package be.cegeka.batchers.taxcalculator.batch.api;

import be.cegeka.batchers.taxcalculator.batch.domain.JobStartParams;

public interface JobService {

    void runTaxCalculatorJob(JobStartParams jobStartParams);

}
