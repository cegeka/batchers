package be.cegeka.batchers.taxcalculator.application.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmployeeGenerator {
    private static final Long GENERATED_COUNT = 21L;

    @Value("${number.of.employees:21}")
    private Long numberOfEmployees;

    @Autowired
    private EmployeeGeneratorService employeeGeneratorService;

    public void generateAll() {
        employeeGeneratorService.generateEmployees(numberOfEmployees);
    }

    public Long getNumberOfEmployees() {
        return numberOfEmployees;
    }

    public void setNumberOfEmployees(Long numberOfEmployees) {
        this.numberOfEmployees = numberOfEmployees;
    }

    void resetSize() {
        numberOfEmployees = GENERATED_COUNT;
    }
}
