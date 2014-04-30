package be.cegeka.batchers.taxcalculator;

import be.cegeka.batchers.taxcalculator.domain.Employee;
import be.cegeka.batchers.taxcalculator.domain.EmployeeRepository;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;

/**
 * Created by andreip on 29.04.2014.
 */
@Component
public class EmployeeReader implements ItemReader<Employee> {

    @Autowired
    private EmployeeRepository employeeRepository;

    private Iterator<Employee> itemIterator;

    @Override
    public Employee read() {
        if(getItemIterator().hasNext()){
            return getItemIterator().next();
        } else {
            return null;
        }
    }

    private Iterator<Employee> getItemIterator() {
        if (itemIterator == null) {
            itemIterator = employeeRepository.getAll().iterator();
        }
        return itemIterator;
    }

    void setEmployeeRepo(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }
}
