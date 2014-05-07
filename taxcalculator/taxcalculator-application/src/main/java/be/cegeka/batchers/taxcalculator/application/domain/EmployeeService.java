package be.cegeka.batchers.taxcalculator.application.domain;

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
}
