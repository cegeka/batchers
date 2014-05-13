package be.cegeka.batchers.taxcalculator.application.domain;

import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

import static java.lang.System.currentTimeMillis;

@Component
public class EmployeeGenerator {
    public static final Long GENERATED_COUNT = 300L;

    private Long size = GENERATED_COUNT;

    Faker faker = new Faker();

    @Autowired
    private EmployeeRepository employeeRepository;

    public void generateAll() {
        for (int i = 0; i < size; i++) {
            Employee employee = new Employee();
            employee.setFirstName(faker.firstName());
            employee.setLastName(faker.lastName());
            employee.setEmail(employee.getFirstName()+"." + employee.getLastName()+"@mailinator.com");
            employee.setIncome(500 + (new Random(currentTimeMillis()).nextInt(4501)));
            employeeRepository.save(employee);
        }
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getSize() {
        return size;
    }

    public void resetSize() {
        size = GENERATED_COUNT;
    }
}
