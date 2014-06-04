package be.cegeka.batchers.taxcalculator.batch.service.reporting;

import be.cegeka.batchers.taxcalculator.batch.repositories.TaxCalculationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SumOfTaxes {
    @Autowired
    private TaxCalculationRepository taxCalculationRepository;

    public double getSuccessSum(int year, int month) {
        return taxCalculationRepository.getSuccessSum(year, month).getAmount().doubleValue();
    }

    public double getFailedSum(int year, int month) {
        return taxCalculationRepository.getFailedSum(year, month).getAmount().doubleValue();
    }
}
