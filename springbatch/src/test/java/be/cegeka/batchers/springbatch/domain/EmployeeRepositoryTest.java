package be.cegeka.batchers.springbatch.domain;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.fest.assertions.Assertions.assertThat;

@ContextConfiguration(locations = "classpath:/be/cegeka/batchers/springbatch/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class EmployeeRepositoryTest {
    public static final int INCOME = 500;

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
    public void testWhenSavingTheEmployeeTheIncomeIsPersisted() throws Exception {
        Employee employee = new Employee();
        employee.setIncome(INCOME);

        repository.save(employee);

        Employee savedEmployee = repository.getBy(employee.getId());
        assertThat(savedEmployee.getIncome()).isEqualTo(INCOME);
    }
}
