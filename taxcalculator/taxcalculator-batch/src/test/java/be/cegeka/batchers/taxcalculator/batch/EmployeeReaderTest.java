package be.cegeka.batchers.taxcalculator.batch;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.domain.EmployeeRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by andreip on 29.04.2014.
 */
public class EmployeeReaderTest {

    @Mock
    EmployeeRepository employeeRepository;

    EmployeeReader employeeReader;
    private ArrayList<Employee> employees;

    @Before
    public void setUp() {
        employeeReader = new EmployeeReader();
        employees = new ArrayList();
        employees.add(new Employee());

        employeeRepository = mock(EmployeeRepository.class);
        when(employeeRepository.getAll()).thenReturn(employees);
        employeeReader.setEmployeeRepo(employeeRepository);
    }

    @Test
    public void testRead() throws Exception {
        Employee readEmployee = employeeReader.read();
        assertEquals("read employee is not equal to given one", employees.get(0), readEmployee);
        readEmployee = employeeReader.read();
        assertEquals("read employee is not equal to given one", null, readEmployee);
    }
}
