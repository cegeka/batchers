package be.cegeka.batchers.taxcalculator.application.domain;

import be.cegeka.batchers.taxcalculator.to.EmployeeTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    EmployeeRepository employeeRepository;

    public List<EmployeeTo> getEmployees(int page, int pageSize) {
        return employeeRepository.getEmployees(page, pageSize);
    }

    public long getEmployeeCount() {
        return employeeRepository.getEmployeeCount();
    }
}
