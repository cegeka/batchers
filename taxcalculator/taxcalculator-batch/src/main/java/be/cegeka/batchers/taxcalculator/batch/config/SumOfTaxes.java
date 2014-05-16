package be.cegeka.batchers.taxcalculator.batch.config;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class SumOfTaxes {
    private double sumOfTaxes;

    public void addToSumOfTaxes(double tax) {
        sumOfTaxes += tax;
    }

    public double getSum() {
        return sumOfTaxes;
    }
}
