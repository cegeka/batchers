package be.cegeka.batchers.taxcalculator.batch.api;

import be.cegeka.batchers.taxcalculator.batch.api.events.JobStartRequest;

public interface JobService {

    void runTaxCalculatorJob(JobStartRequest jobStartRequest);

}
