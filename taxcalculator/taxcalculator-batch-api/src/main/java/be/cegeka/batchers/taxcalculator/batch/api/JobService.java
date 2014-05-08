package be.cegeka.batchers.taxcalculator.batch.api;

import java.util.List;

public interface JobService {
    void runTaxCalculatorJob();


    //todo : or maybe have something generic
//    void run(String jobName);
//    List<String> getJobNames();
}
