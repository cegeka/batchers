package be.cegeka.batchers.taxcalculator.application.domain;

import be.cegeka.batchers.taxcalculator.to.EmployeeTo;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class EmployeeMapperTest {
    public static final String FIRST_NAME = "first name";
    public static final String LAST_NAME = "last name";
    public static final String SOME_EMAIL_COM = "some@email.com";

    private EmployeeMapper mapper = new EmployeeMapper();

    @Test
    public void testMapToTo() throws Exception {
        Employee employee = new Employee();
        employee.setFirstName(FIRST_NAME);
        employee.setLastName(LAST_NAME);
        employee.setEmail(SOME_EMAIL_COM);

        EmployeeTo employeeTo = mapper.toTo(employee);
        assertThat(employeeTo.getEmail()).isEqualTo(SOME_EMAIL_COM);
        assertThat(employeeTo.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(employeeTo.getLastName()).isEqualTo(LAST_NAME);
    }
}