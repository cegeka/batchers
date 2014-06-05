package be.cegeka.batchers.taxcalculator.application.service;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.domain.EmployeeRepository;
import be.cegeka.batchers.taxcalculator.application.domain.generation.EmployeeGeneratorCleaner;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

import static java.lang.System.currentTimeMillis;

@Service
public class EmployeeGeneratorService {

    public static final Long MAX_GENERATED_EMPLOYEES = 300000L;
    private Faker faker = new Faker();

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private List<EmployeeGeneratorCleaner> employeeGeneratorCleaners;

    @Transactional
    public void resetEmployees(Long numberOfEmployees) {

        if (numberOfEmployees < 1 || numberOfEmployees > MAX_GENERATED_EMPLOYEES) {
            throw new IllegalArgumentException("The number of generated employees must be between 1 and " + MAX_GENERATED_EMPLOYEES);
        }

        deleteEmployeesAndEverythingRelated();
        generateNewEmployees(numberOfEmployees);
    }

    private void generateNewEmployees(Long numberOfEmployees) {
        for (int i = 0; i < numberOfEmployees; i++) {
            Employee employee = new Employee();
            employee.setFirstName(faker.firstName());
            employee.setLastName(faker.lastName());
            employee.setEmail(employee.getFirstName() + "." + employee.getLastName() + "@mailinator.com");
            employee.setIncome(500 + (new Random(currentTimeMillis()).nextInt(4501)));
            employeeRepository.save(employee);
        }
    }


    private void deleteEmployeesAndEverythingRelated() {
        employeeGeneratorCleaners.forEach(EmployeeGeneratorCleaner::deleteAll);
    }

}
