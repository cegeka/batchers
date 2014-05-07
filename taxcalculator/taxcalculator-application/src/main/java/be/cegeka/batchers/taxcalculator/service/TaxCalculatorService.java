package be.cegeka.batchers.taxcalculator.service;

import be.cegeka.batchers.taxcalculator.domain.Employee;
import be.cegeka.batchers.taxcalculator.domain.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaxCalculatorService {

    @Autowired
    RunningTimeService runningTimeService;

    @Autowired
    EmployeeRepository employeeRepository;

    public void calculateTax(Employee employee) {
        employee.addTax();
        employeeRepository.save(employee);
        runningTimeService.sleep();
    }

    public void setRunningTimeService(RunningTimeService runningTimeService) {
        this.runningTimeService = runningTimeService;
    }

    public void setEmployeeRepository(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }
}
