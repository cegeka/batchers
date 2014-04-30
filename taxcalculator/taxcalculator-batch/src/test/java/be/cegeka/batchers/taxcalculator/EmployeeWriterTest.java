package be.cegeka.batchers.taxcalculator;

import be.cegeka.batchers.taxcalculator.domain.Employee;
import be.cegeka.batchers.taxcalculator.domain.EmployeeRepository;
import org.junit.*;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Created by andreip on 29.04.2014.
 */
public class EmployeeWriterTest {

    @Mock
    EmployeeRepository employeeRepository;
    EmployeeWriter employeeWriter;

    @Before
    public void setUp() {
        employeeRepository = mock(EmployeeRepository.class);
        employeeWriter = new EmployeeWriter();
        employeeWriter.setEmployeeRepository(employeeRepository);
    }

    @Test
    public void testWrite() throws Exception {
        List<Employee> employees = new ArrayList<>();
        Employee employee1 = getEmployee(1);
        employees.add(employee1);
        Employee employee2 = getEmployee(2);
        employees.add(employee2);
        Employee employee3 = getEmployee(3);
        employees.add(employee3);

        employeeWriter.write(employees);

        verify(employeeRepository).save(employee1);
        verify(employeeRepository).save(employee2);
        verify(employeeRepository).save(employee3);
    }

    private Employee getEmployee(long employeeNumber) {
        Employee employee = new Employee();
        employee.setId(employeeNumber);
        employee.setFirstName("first name " + employeeNumber);
        employee.setLastName("last name " + employeeNumber);
        return employee;
    }
}
