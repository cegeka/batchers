package be.cegeka.batchers.taxcalculator;

import be.cegeka.batchers.taxcalculator.domain.Employee;
import be.cegeka.batchers.taxcalculator.service.TaxCalculatorService;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by andreip on 29.04.2014.
 */
@Component
public class EmployeeProcessor implements ItemProcessor<Employee, Employee> {

    @Autowired
    TaxCalculatorService taxCalculatorService;

    @Override
    public Employee process(Employee item) {
        taxCalculatorService.calculateTax(item);
        return item;
    }

}
