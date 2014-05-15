package be.cegeka.batchers.taxcalculator.application.domain;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class EmployeeTest {

    @Test
    public void testFullName() throws Exception {
        Employee employee = new Employee();
        employee.setFirstName("XYZ");
        employee.setLastName("ABC");
        assertThat(employee.fullName()).isEqualTo("XYZ ABC");

    }
}
