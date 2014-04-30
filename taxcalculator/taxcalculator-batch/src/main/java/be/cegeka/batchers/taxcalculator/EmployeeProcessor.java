package be.cegeka.batchers.taxcalculator;

import be.cegeka.batchers.taxcalculator.domain.Employee;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * Created by andreip on 29.04.2014.
 */
@Component
public class EmployeeProcessor implements ItemProcessor<Employee, Employee> {

    @Override
    public Employee process(Employee item) {
        item.addTax();
        return item;
    }

}
