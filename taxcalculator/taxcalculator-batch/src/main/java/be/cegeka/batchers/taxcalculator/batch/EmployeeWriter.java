package be.cegeka.batchers.taxcalculator.batch;

import be.cegeka.batchers.taxcalculator.domain.Employee;
import be.cegeka.batchers.taxcalculator.domain.EmployeeRepository;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmployeeWriter implements ItemWriter<Employee> {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public void write(List<? extends Employee> items) throws Exception {
        items.forEach(employeeRepository::save);
    }

    public void setEmployeeRepository(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }
}
