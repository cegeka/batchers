package be.cegeka.batchers.springbatch.domain;


import be.cegeka.batchers.springbatch.infrastructure.IntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.fest.assertions.Assertions.assertThat;

public class EmployeeRepositoryTest extends IntegrationTest {
    public static final int INCOME = 500;
    public static final String FIRST_NAME = "FirstName";
    public static final String LAST_NAME = "LastName";
    public static final String SOME_ADDRESS = "Some address";

    @Autowired
    EmployeeRepository repository;

    @Test
    public void testRepositoryIsNotNull() throws Exception {
        assertThat(repository).isNotNull();
    }

    @Test
    public void testWhenSavingEmployeeTheIdIsNotNull() throws Exception {
        Employee employee = new Employee();

        repository.save(employee);

        assertThat(employee.getId()).isNotNull();
    }

    @Test
    public void testWhenSavingTheEmployeeIsPersisted() throws Exception {
        Employee employee = new Employee();
        employee.setIncome(INCOME);
        employee.setFirstName(FIRST_NAME);
        employee.setLastName(LAST_NAME);
        employee.setAddress(SOME_ADDRESS);

        repository.save(employee);

        Employee savedEmployee = repository.getBy(employee.getId());
        assertThat(savedEmployee.getIncome()).isEqualTo(INCOME);
        assertThat(savedEmployee.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(savedEmployee.getLastName()).isEqualTo(LAST_NAME);
        assertThat(savedEmployee.getTaxTotal()).isEqualTo(new Integer(0));
        assertThat(savedEmployee.getAddress()).isEqualTo(SOME_ADDRESS);
    }

    @Test
    public void testWhenSettingRetirementSavingsTheCalculationDateIsCurrentDate() throws Exception {
        Employee employee = new Employee();
        employee.setTaxTotal(34);
        repository.save(employee);

        Employee saved = repository.getBy(employee.getId());
        assertThat(saved.getCalculationDate().isBeforeNow()).isTrue();
    }
}
