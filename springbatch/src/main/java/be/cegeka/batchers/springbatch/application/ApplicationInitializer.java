package be.cegeka.batchers.springbatch.application;

import be.cegeka.batchers.springbatch.domain.EmployeeGenerator;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class ApplicationInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private EmployeeGenerator employeeGenerator;

    private boolean generateEmployees = true;

    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (generateEmployees) {
            employeeGenerator.generateAll();
        }
    }

    public void setEmployeeGenerator(EmployeeGenerator employeeGenerator) {
        this.employeeGenerator = employeeGenerator;
    }

    public void setGenerateEmployees(boolean generateEmployees) {
        this.generateEmployees = generateEmployees;
    }
}
