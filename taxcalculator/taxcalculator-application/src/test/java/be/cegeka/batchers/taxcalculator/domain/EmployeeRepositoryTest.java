package be.cegeka.batchers.taxcalculator.domain;

import be.cegeka.batchers.taxcalculator.infrastructure.IntegrationTest;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.web.WebAppConfiguration;

import java.math.BigDecimal;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class EmployeeRepositoryTest extends IntegrationTest {
    public static final int INCOME = 500;
    public static final String FIRST_NAME = "FirstName";
    public static final String LAST_NAME = "LastName";

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

        repository.save(employee);

        Employee savedEmployee = repository.getBy(employee.getId());
        assertThat(savedEmployee.getIncome()).isEqualTo(INCOME);
        assertThat(savedEmployee.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(savedEmployee.getLastName()).isEqualTo(LAST_NAME);
        assertThat(savedEmployee.getTaxTotal()).isEqualTo(Money.zero(CurrencyUnit.EUR));
    }

    @Test
    public void testWhenSettingRetirementSavingsTheCalculationDateIsCurrentDate() throws Exception {
        Employee employee = new Employee();
        employee.setIncome(100);
        employee.addTax();
        repository.save(employee);

        Employee saved = repository.getBy(employee.getId());
        assertThat(saved.getCalculationDate().isBeforeNow()).isTrue();
    }

    @Test
    public void testCount() throws Exception {
        Employee first = new Employee();
        Employee second = new Employee();

        repository.save(first);
        repository.save(second);

        assertThat(repository.count()).isEqualTo(2L);
    }

    @Test
    public void testGetAll() throws Exception {
        Employee first = new Employee();
        Employee second = new Employee();

        repository.save(first);
        repository.save(second);

        assertThat(repository.getAll()).containsOnly(first, second);
    }

    @Test
    public void testGetAllIsEmptyWhenNoEmployees() throws Exception {
        assertThat(repository.getAll()).isEmpty();
    }

    @Test
    public void testGetFirst20() throws Exception {
        for (int i = 0; i < 30; i++) {
            repository.save(new Employee());
        }

        List<Employee> first20 = repository.getFirst20();
        assertThat(first20).hasSize(20);
    }
}
