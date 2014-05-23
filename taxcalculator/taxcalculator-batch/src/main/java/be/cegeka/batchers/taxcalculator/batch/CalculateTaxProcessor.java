package be.cegeka.batchers.taxcalculator.batch;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.domain.TaxCalculation;
import be.cegeka.batchers.taxcalculator.application.service.TaxCalculatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class CalculateTaxProcessor extends StepExecutionListenerSupport implements ItemProcessor<Employee, TaxCalculation> {
    private static final Logger LOG = LoggerFactory.getLogger(CalculateTaxProcessor.class);

    @Autowired
    TaxCalculatorService taxCalculatorService;
    @Value("#{jobParameters[year]}")
    private long year;
    @Value("#{jobParameters[month]}")
    private long month;

    private StepExecution stepExecution;

    @Override
    public TaxCalculation process(Employee employee) {
        LOG.info("Tax process: " + employee);
        return taxCalculatorService.calculateTax(stepExecution.getJobExecutionId(), employee, year, month);
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }
}
