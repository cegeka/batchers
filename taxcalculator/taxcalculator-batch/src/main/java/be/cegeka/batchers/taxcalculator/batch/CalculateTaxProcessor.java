package be.cegeka.batchers.taxcalculator.batch;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.service.TaxCalculatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CalculateTaxProcessor implements ItemProcessor<Employee, Employee> {
    private static final Logger LOG = LoggerFactory.getLogger(CalculateTaxProcessor.class);

    @Autowired
    TaxCalculatorService taxCalculatorService;

    @Override
    public Employee process(Employee item) {
        LOG.info("Tax process: " + item);
        taxCalculatorService.calculateTax(item);
        return item;
    }
}
