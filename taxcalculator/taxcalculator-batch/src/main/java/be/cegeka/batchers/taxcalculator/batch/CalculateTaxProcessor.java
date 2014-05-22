package be.cegeka.batchers.taxcalculator.batch;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.domain.TaxCalculation;
import be.cegeka.batchers.taxcalculator.application.service.TaxCalculatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class CalculateTaxProcessor implements ItemProcessor<Employee, TaxCalculation> {
    private static final Logger LOG = LoggerFactory.getLogger(CalculateTaxProcessor.class);
    @Autowired
    TaxCalculatorService taxCalculatorService;
    @Value("#{jobParameters[year]}")
    private int year;
    @Value("#{jobParameters[month]}")
    private int month;

    @Override
    public TaxCalculation process(Employee employee) {
        LOG.info("Tax process: " + employee);
        return taxCalculatorService.calculateTax(employee, year, month);
    }
}
