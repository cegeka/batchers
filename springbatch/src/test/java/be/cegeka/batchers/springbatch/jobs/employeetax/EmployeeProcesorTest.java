package be.cegeka.batchers.springbatch.jobs.employeetax;

import be.cegeka.batchers.springbatch.domain.Employee;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Created by andreip on 29.04.2014.
 */
public class EmployeeProcesorTest {

    private EmployeeProcessor employeeProcessor;
    private ArrayList<Employee> employees;

    @Before
    public void setUp() {
        employeeProcessor = new EmployeeProcessor();
        employees = new ArrayList();
        employees.add(new Employee());
    }

    @Test
    public void testRead() throws Exception {
        int income1 = 1000;
        int income2 = 1500;
        Employee employee1 = new Employee();
        employee1.setIncome(income1);
        Employee employee2 = new Employee();
        employee2.setIncome(income2);

        employee1 = employeeProcessor.process(employee1);
        employee2 = employeeProcessor.process(employee2);

        assertEquals("processed employee tax is not equal to given one", (int) (income1 * 0.1), employee1.getTaxTotal());
        assertEquals("processed employee tax is not equal to given one", (int) (income2 * 0.1), employee2.getTaxTotal());
    }
}
