package be.cegeka.batchers.taxcalculator.batch.service.reporting;

import be.cegeka.batchers.taxcalculator.application.domain.TaxServiceCallResultRepository;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SumOfTaxes {
    @Autowired
    TaxServiceCallResultRepository taxServiceCallResultRepository;

    public double getSuccessSum(long year, long month) {
        return taxServiceCallResultRepository.getSuccessSum(year, month).getAmount().doubleValue();
    }

    public double getFailedSum(long year, long month) {
        return taxServiceCallResultRepository.getFailedSum(year, month).getAmount().doubleValue();
    }
}
