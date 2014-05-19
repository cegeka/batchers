package be.cegeka.batchers.taxcalculator.batch.service.reporting;

import org.springframework.stereotype.Component;

@Component
public class SumOfTaxes {
    private double successSum;
    private double failedSum;

    public void addToSuccessSum(double tax) {
        successSum += tax;
    }

    public double getSuccessSum() {
        return successSum;
    }

    public void reset() {
        successSum = 0;
        failedSum = 0;
    }

    public double getFailedSum() {
        return failedSum;
    }

    public void addToFailingSum(double incomeTax) {
        failedSum += incomeTax;
    }
}
