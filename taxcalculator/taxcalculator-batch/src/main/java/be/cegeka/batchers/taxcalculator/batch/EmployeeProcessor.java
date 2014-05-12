package be.cegeka.batchers.taxcalculator.batch;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.service.TaxCalculatorService;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmployeeProcessor implements ItemProcessor<Employee, Employee> {

    @Autowired
    TaxCalculatorService taxCalculatorService;

    @Override
    public Employee process(Employee item) {
        System.out.println(item);
        taxCalculatorService.calculateTax(item);
        return item;
    }

}
