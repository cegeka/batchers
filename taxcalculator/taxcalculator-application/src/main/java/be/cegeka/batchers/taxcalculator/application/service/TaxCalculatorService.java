package be.cegeka.batchers.taxcalculator.application.service;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.domain.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaxCalculatorService {

    @Autowired
    RunningTimeService runningTimeService;

    public void calculateTax(Employee employee) {
        employee.addTax();
        runningTimeService.sleep();
    }

    public void setRunningTimeService(RunningTimeService runningTimeService) {
        this.runningTimeService = runningTimeService;
    }
}
