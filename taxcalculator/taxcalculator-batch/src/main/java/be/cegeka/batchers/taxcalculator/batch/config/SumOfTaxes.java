package be.cegeka.batchers.taxcalculator.batch.config;

import org.springframework.stereotype.Component;

@Component
public class SumOfTaxes {
    private double successSum;

    public void addToSuccessSum(double tax) {
        successSum += tax;
    }

    public double getSuccessSum() {
        return successSum;
    }

    public void reset() {
        successSum = 0;
    }
}
