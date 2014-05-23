package be.cegeka.batchers.taxcalculator.batch.service.reporting;

import be.cegeka.batchers.taxcalculator.application.domain.TaxServiceCallResultRepository;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class SumOfTaxes {
    @Value("#{jobParameters[year]}")
    private int year;
    @Value("#{jobParameters[month]}")
    private int month;

    @Autowired
    TaxServiceCallResultRepository taxServiceCallResultRepository;

    public double getSuccessSum() {
        return taxServiceCallResultRepository.getSuccessSum(year, month).getAmount().doubleValue();
    }

    public double getFailedSum() {
        return taxServiceCallResultRepository.getFailedSum(year, month).getAmount().doubleValue();
    }
}
