package be.cegeka.batchers.taxcalculator.application.domain;

import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Random;

import static java.lang.System.currentTimeMillis;

@Component
public class EmployeeGenerator {
    private static final Long GENERATED_COUNT = 21L;

    @Value("${number.of.employees:21}")
    protected Long numberOfEmployees;

    private Faker faker = new Faker();

    @Autowired
    private EmployeeRepository employeeRepository;

    public void generateAll() {
        Random random = new Random(currentTimeMillis());

        for (int i = 0; i < numberOfEmployees; i++) {
            Employee employee = new Employee();
            employee.setFirstName(faker.firstName());
            employee.setLastName(faker.lastName());
            employee.setEmail(employee.getFirstName() + "." + employee.getLastName() + "@mailinator.com");
            employee.setIncome(500 + (random.nextInt(4501)));
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
