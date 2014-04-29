package be.cegeka.batchers.springbatch.jobs.employeetax;

import be.cegeka.batchers.springbatch.domain.Employee;
import be.cegeka.batchers.springbatch.domain.EmployeeRepository;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by andreip on 29.04.2014.
 */
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
