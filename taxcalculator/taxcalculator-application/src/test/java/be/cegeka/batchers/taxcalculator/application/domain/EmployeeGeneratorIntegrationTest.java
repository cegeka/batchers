package be.cegeka.batchers.taxcalculator.application.domain;


import be.cegeka.batchers.taxcalculator.application.infrastructure.IntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class EmployeeGeneratorIntegrationTest extends IntegrationTest {
    private static final long SIZE = 7L;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeGenerator employeeGenerator;


    @Test
    public void testEmployeeGenerator() {
        employeeGenerator.setNumberOfEmployees(SIZE);
        employeeGenerator.generateAll();

        Long count = employeeRepository.count();
        assertThat(count).isEqualTo(SIZE);
        employeeGenerator.resetSize();
    }

    @Test
    public void testNumberOfEmployeesIsReadFromPropertyFile() throws Exception {
        assertThat(employeeGenerator.getNumberOfEmployees()).isEqualTo(12);
    }

    @Test
    public void testEmployeeGeneratorSetsNameAndIncome() {
        employeeGenerator.setNumberOfEmployees(2L);
        employeeGenerator.generateAll();

        List<Employee> all = employeeRepository.getAll();
        for (Employee employee : all) {
            assertThat(employee.getLastName()).isNotNull();
            assertThat(employee.getFirstName()).isNotNull();
            assertThat(employee.getEmail()).isNotNull();
            assertThat(employee.getIncome()).isGreaterThanOrEqualTo(500);
            assertThat(employee.getIncome()).isLessThanOrEqualTo(5000);
        }

        employeeGenerator.resetSize();
    }
}
