package be.cegeka.batchers.taxcalculator.application.domain;

import be.cegeka.batchers.taxcalculator.to.EmployeeTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    EmployeeRepository employeeRepository;

    public List<Employee> getFirst20() {
        return employeeRepository.getFirst20();
    }

    public List<EmployeeTo> getFirst20WithTax() {
        return employeeRepository.getFirst20WithTax();
    }
}
