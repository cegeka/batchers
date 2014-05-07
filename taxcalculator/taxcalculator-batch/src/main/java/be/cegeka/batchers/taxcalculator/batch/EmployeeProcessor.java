package be.cegeka.batchers.taxcalculator.batch;

import be.cegeka.batchers.taxcalculator.domain.Employee;
import be.cegeka.batchers.taxcalculator.service.TaxCalculatorService;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
