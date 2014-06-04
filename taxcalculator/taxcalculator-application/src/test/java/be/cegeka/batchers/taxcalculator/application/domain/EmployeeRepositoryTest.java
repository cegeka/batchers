package be.cegeka.batchers.taxcalculator.application.domain;

import be.cegeka.batchers.taxcalculator.application.infrastructure.IntegrationTest;
import be.cegeka.batchers.taxcalculator.to.EmployeeTo;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class EmployeeRepositoryTest extends IntegrationTest {
    private static final int INCOME = 500;
    private static final String FIRST_NAME = "FirstName";
    private static final String LAST_NAME = "LastName";

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private TaxCalculationRepository taxCalculationRepository;

    private List<Long> employeeIds = new ArrayList<>();

    @Test
    public void testRepositoryIsNotNull() throws Exception {
        assertThat(employeeRepository).isNotNull();
    }

    @Test
    public void testWhenSavingEmployeeTheIdIsNotNull() throws Exception {
        Employee employee = new EmployeeTestBuilder().build();

        employeeRepository.save(employee);

        assertThat(employee.getId()).isNotNull();
    }

    @Test
    public void testWhenSavingTheEmployeeIsPersisted() throws Exception {
        Employee employee = new EmployeeTestBuilder()
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
        Employee first = new EmployeeTestBuilder().build();
        Employee second = new EmployeeTestBuilder().build();

        employeeRepository.save(first);
        employeeRepository.save(second);

        assertThat(employeeRepository.count()).isEqualTo(2L);
    }

    @Test
    public void testDeleteAll() throws Exception {
        Employee first = new EmployeeTestBuilder().build();
        Employee second = new EmployeeTestBuilder().build();

        employeeRepository.save(first);
        employeeRepository.save(second);

        employeeRepository.deleteAll();
        assertThat(employeeRepository.count()).isEqualTo(0L);
    }

    @Test
    public void testGetAll() throws Exception {
        Employee first = new EmployeeTestBuilder().build();
        Employee second = new EmployeeTestBuilder().build();

        employeeRepository.save(first);
        employeeRepository.save(second);

        assertThat(employeeRepository.getAll()).containsOnly(first, second);
    }

    @Test
    public void testGetAllIsEmptyWhenNoEmployees() throws Exception {
        assertThat(employeeRepository.getAll()).isEmpty();
    }

    @Test
    public void given30Employees_whenGetEmployeesForFirstPage_thenFirst10EmployeesAreReturned() throws Exception {
        haveEmployees(30, true);

        List<EmployeeTo> first10 = employeeRepository.getEmployees(0, 10);
        assertThat(first10).hasSize(10);
        assertThat(first10.get(0).getEmail()).isEqualTo("john.smith0@gmail.com");
    }

    @Test
    public void givenEmployeesWithoutCalculatedTaxes_whenGetEmployeesForSecondPage_thenSecondPageEmployeesAreReturnedWithTaxZero() {
        haveEmployees(30, false);

        List<EmployeeTo> employeesSecondPage = employeeRepository.getEmployees(1, 10);
        assertThat(employeesSecondPage).hasSize(10);
        assertThat(employeesSecondPage.get(0).getEmail()).isEqualTo("john.smith10@gmail.com");
    }

    @Test
    public void given20Employees_whenGetCount_thenEmployeeCountIs20() {
        haveEmployees(20, false);

        assertThat(employeeRepository.getEmployeeCount()).isEqualTo(20L);
    }

    @Test
    public void given5Employees_whenGetEmployeeIds_thenReturnCorrectList() {
        haveEmployees(5, false);

        assertThat(employeeRepository.getEmployeeIds(2014L, 5L, 0L)).isEqualTo(employeeIds);
    }

    private void haveEmployees(int employeeCount, boolean withTax) {
        for (int i = 0; i < employeeCount; i++) {
            Employee employee = new EmployeeTestBuilder()
                    .withFirstName("John" + i)
                    .withLastName("Smith" + i)
                    .withEmailAddress("john.smith" + i + "@gmail.com")
                    .build();
            employeeRepository.save(employee);

            employeeIds.add(employee.getId());

            if (withTax) {
                taxCalculationRepository.save(new TaxCalculationTestBuilder().withEmployee(employee).withYear(2014).withMonth(5).withTax(100.0).build());
            }
        }
    }

}
