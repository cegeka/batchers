package be.cegeka.batchers.taxcalculator.application.domain;

import be.cegeka.batchers.taxcalculator.to.EmployeeTo;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public EmployeeTo toTo(Employee employee) {
        EmployeeTo employeeTo = new EmployeeTo();
        employeeTo.setEmail(employee.getEmail());
        employeeTo.setFirstName(employee.getFirstName());
        employeeTo.setLastName(employee.getLastName());
        return employeeTo;
    }
}
