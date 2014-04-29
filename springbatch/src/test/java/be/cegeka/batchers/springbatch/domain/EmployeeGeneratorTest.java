package be.cegeka.batchers.springbatch.domain;

import be.cegeka.batchers.springbatch.infrastructure.IntegrationTest;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class EmployeeGeneratorTest extends IntegrationTest {
    public static final long SIZE = 7L;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeGenerator employeeGenerator;

    @Test
    public void testDefaultSize() throws Exception {
        assertThat(employeeGenerator.getSize()).isEqualTo(EmployeeGenerator.GENERATED_COUNT);
    }

    @Test
    public void testEmployeeGenerator() {
        employeeGenerator.setSize(SIZE);
        employeeGenerator.generateAll();

        Long count = employeeRepository.count();
        assertThat(count).isEqualTo(SIZE);
        employeeGenerator.resetSize();
    }

    @Test
    public void testEmployeeGeneratorSetsNameAndIncome() {
        employeeGenerator.setSize(2L);
        employeeGenerator.generateAll();

        List<Employee> all = employeeRepository.getAll();
        for (Employee employee : all) {
            assertThat(employee.getLastName()).isNotNull();
            assertThat(employee.getFirstName()).isNotNull();
            assertThat(employee.getIncome()).isGreaterThanOrEqualTo(500);
            assertThat(employee.getIncome()).isLessThanOrEqualTo(5000);
            assertThat(employee.getTaxTotal()).isEqualTo(Money.zero(CurrencyUnit.EUR));
        }

        employeeGenerator.resetSize();
    }
}
