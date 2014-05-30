package be.cegeka.batchers.taxcalculator.batch.api;

public interface JobService {

    void runTaxCalculatorJob(long year, long month);

}
