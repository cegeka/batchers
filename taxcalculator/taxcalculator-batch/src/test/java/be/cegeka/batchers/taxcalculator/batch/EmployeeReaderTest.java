package be.cegeka.batchers.taxcalculator.batch;

import be.cegeka.batchers.taxcalculator.batch.EmployeeReader;
import be.cegeka.batchers.taxcalculator.domain.Employee;
import be.cegeka.batchers.taxcalculator.domain.EmployeeRepository;
import org.fest.assertions.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class EmployeeReaderTest {

    private EmployeeReader employeeReader;

    @Mock
    private EmployeeRepository employeeRepository;

    private ArrayList<Employee> employees;

    @Before
    public void setUp() {
        employeeReader = new EmployeeReader();
        employees = new ArrayList();
        employees.add(new Employee());

        when(employeeRepository.getAll()).thenReturn(employees);
        employeeReader.setEmployeeRepo(employeeRepository);
    }

    @Test
    public void testRead() throws Exception {
        Employee readEmployee = employeeReader.read();
        assertThat(employees.get(0)).isEqualTo(readEmployee);

        readEmployee = employeeReader.read();
        assertThat(readEmployee).isNull();
    }
}
