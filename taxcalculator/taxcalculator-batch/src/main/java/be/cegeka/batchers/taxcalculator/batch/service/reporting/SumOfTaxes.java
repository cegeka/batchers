package be.cegeka.batchers.taxcalculator.batch.service.reporting;

import be.cegeka.batchers.taxcalculator.application.domain.MonthlyReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SumOfTaxes {
    @Autowired
    MonthlyReportRepository monthlyReportRepository;

    public double getSuccessSum(int year, int month) {
        return monthlyReportRepository.getSuccessSum(year, month).getAmount().doubleValue();
    }

    public double getFailedSum(int year, int month) {
        return monthlyReportRepository.getFailedSum(year, month).getAmount().doubleValue();
    }
}
