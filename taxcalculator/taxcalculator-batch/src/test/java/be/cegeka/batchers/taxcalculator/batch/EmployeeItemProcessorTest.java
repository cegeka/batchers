package be.cegeka.batchers.taxcalculator.batch;

import be.cegeka.batchers.taxcalculator.batch.EmployeeItemProcessor;
import be.cegeka.batchers.taxcalculator.domain.Employee;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EmployeeItemProcessorTest {

    @Test
    public void testProcessedPersonRecord() throws Exception {
        Employee employee = new Employee();
        employee.setFirstName("Jane");
        employee.setLastName("Doe");

        Employee processedEmployee = new EmployeeItemProcessor().process(employee);

        assertEquals("JANE", processedEmployee.getFirstName());
        assertEquals("DOE", processedEmployee.getLastName());
    }
}
