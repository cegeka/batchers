package be.cegeka.batchers.taxcalculator.application.domain;

import be.cegeka.batchers.taxcalculator.application.infrastructure.IntegrationTest;
import be.cegeka.batchers.taxcalculator.to.EmployeeTo;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class EmployeeRepositoryTest extends IntegrationTest {
    public static final int INCOME = 500;
    public static final String FIRST_NAME = "FirstName";
    public static final String LAST_NAME = "LastName";

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    TaxCalculationRepository taxCalculationRepository;

    @Test
    public void testRepositoryIsNotNull() throws Exception {
        assertThat(employeeRepository).isNotNull();
    }

    @Test
    public void testWhenSavingEmployeeTheIdIsNotNull() throws Exception {
        Employee employee = new EmployeeBuilder().build();

        employeeRepository.save(employee);

        assertThat(employee.getId()).isNotNull();
    }

    @Test
    public void testWhenSavingTheEmployeeIsPersisted() throws Exception {
        Employee employee = new EmployeeBuilder()
                .withIncome(INCOME)
                .withFirstName(FIRST_NAME)
                .withLastName(LAST_NAME)
                .build();

        employeeRepository.save(employee);

        Employee savedEmployee = employeeRepository.getBy(employee.getId());
        assertThat(savedEmployee.getIncome()).isEqualTo(INCOME);
        assertThat(savedEmployee.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(savedEmployee.getLastName()).isEqualTo(LAST_NAME);
    }

    @Test
    public void testCount() throws Exception {
        Employee first = new EmployeeBuilder().build();
        Employee second = new EmployeeBuilder().build();

        employeeRepository.save(first);
        employeeRepository.save(second);

        assertThat(employeeRepository.count()).isEqualTo(2L);
    }

    @Test
    public void testDeleteAll() throws Exception {
        Employee first = new EmployeeBuilder().build();
        Employee second = new EmployeeBuilder().build();

        employeeRepository.save(first);
        employeeRepository.save(second);

        employeeRepository.deleteAll();
        assertThat(employeeRepository.count()).isEqualTo(0L);
    }

    @Test
    public void testGetAll() throws Exception {
        Employee first = new EmployeeBuilder().build();
        Employee second = new EmployeeBuilder().build();

        employeeRepository.save(first);
        employeeRepository.save(second);

        assertThat(employeeRepository.getAll()).containsOnly(first, second);
    }

    @Test
    public void testGetAllIsEmptyWhenNoEmployees() throws Exception {
        assertThat(employeeRepository.getAll()).isEmpty();
    }

    @Test
    public void testGetFirst20() throws Exception {
        for (int i = 0; i < 30; i++) {
            Employee employee = new EmployeeBuilder()
                    .withFirstName("John" + i)
                    .withLastName("Smith" + i)
                    .withEmailAddress("john.smith" + i + "@gmail.com")
                    .build();
            employeeRepository.save(employee);
            taxCalculationRepository.save(TaxCalculation.from(1L, employee, 2014, 5, Money.of(CurrencyUnit.EUR, new BigDecimal(100))));
        }

        List<EmployeeTo> first20 = employeeRepository.getFirst20();
        assertThat(first20).hasSize(20);
    }

    @Test
    public void givenEmployeesWithoutCalculatedTaxes_whenGetFirst20_thenAllEmployeesAreReturnedWithTaxZero() {
        for (int i = 0; i < 30; i++) {
            Employee employee = new EmployeeBuilder()
                    .withFirstName("John" + i)
                    .withLastName("Smith" + i)
                    .withEmailAddress("john.smith" + i + "@gmail.com")
                    .build();
            employeeRepository.save(employee);
        }

        List<EmployeeTo> first20 = employeeRepository.getFirst20();
        assertThat(first20).hasSize(20);
    }
}
