package be.cegeka.batchers.taxcalculator.application.domain;

import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

import static java.lang.System.currentTimeMillis;

@Service
public class EmployeeGeneratorService {

    public static final Long MAX_GENERATED_EMPLOYEES = 300000L;
    private Faker faker = new Faker();

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeService employeeService;

    public void generateEmployees(Long numberOfEmployees) {

        if (numberOfEmployees < 1 || numberOfEmployees > MAX_GENERATED_EMPLOYEES) {
            throw new IllegalArgumentException("The number of generated employees must be between 1 and " + MAX_GENERATED_EMPLOYEES);
        }

        employeeService.truncate();

        for (int i = 0; i < numberOfEmployees; i++) {
            Employee employee = new Employee();
            employee.setFirstName(faker.firstName());
            employee.setLastName(faker.lastName());
            employee.setEmail(employee.getFirstName() + "." + employee.getLastName() + "@mailinator.com");
            employee.setIncome(500 + (new Random(currentTimeMillis()).nextInt(4501)));
            employeeRepository.save(employee);
        }
    }

}
