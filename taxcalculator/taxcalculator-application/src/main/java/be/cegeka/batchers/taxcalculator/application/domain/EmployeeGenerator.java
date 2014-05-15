package be.cegeka.batchers.taxcalculator.application.domain;

import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Random;

import static java.lang.System.currentTimeMillis;

@Component
public class EmployeeGenerator {
    public static final Long GENERATED_COUNT = 21L;


    @Value("${number.of.employees:21}")
    Long numberOfEmployees;

    Faker faker = new Faker();

    @Autowired
    private EmployeeRepository employeeRepository;

    public void generateAll() {
        for (int i = 0; i < numberOfEmployees; i++) {
            Employee employee = new Employee();
            employee.setFirstName(faker.firstName());
            employee.setLastName(faker.lastName());
            employee.setEmail(employee.getFirstName() + "." + employee.getLastName() + "@mailinator.com");
            employee.setIncome(500 + (new Random(currentTimeMillis()).nextInt(4501)));
            employeeRepository.save(employee);
        }
    }

    public void setNumberOfEmployees(Long numberOfEmployees) {
        this.numberOfEmployees = numberOfEmployees;
    }

    void resetSize() {
        numberOfEmployees = GENERATED_COUNT;
    }
}
