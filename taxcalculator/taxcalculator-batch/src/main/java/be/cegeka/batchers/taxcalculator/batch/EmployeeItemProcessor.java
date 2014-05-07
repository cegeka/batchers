package be.cegeka.batchers.taxcalculator.batch;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.ItemProcessor;

public class EmployeeItemProcessor implements ItemProcessor<Employee, Employee> {
    private static Log LOG = LogFactory.getLog(EmployeeItemProcessor.class);

    @Override
    public Employee process(Employee employee) throws Exception {
        String firstName = employee.getFirstName().toUpperCase();
        String lastName = employee.getLastName().toUpperCase();

        Employee transformedEmployee = new Employee();
        transformedEmployee.setFirstName(firstName);
        transformedEmployee.setLastName(lastName);

        LOG.info("Transformed person: " + employee + " Into: " + transformedEmployee);

        return transformedEmployee;
    }
}
