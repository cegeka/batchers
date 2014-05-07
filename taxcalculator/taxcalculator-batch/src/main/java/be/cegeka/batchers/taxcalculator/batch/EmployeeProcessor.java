package be.cegeka.batchers.taxcalculator.batch;

import be.cegeka.batchers.taxcalculator.domain.Employee;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class EmployeeProcessor implements ItemProcessor<Employee, Employee> {

    @Override
    public Employee process(Employee item) {
        item.addTax();
        return item;
    }

}
