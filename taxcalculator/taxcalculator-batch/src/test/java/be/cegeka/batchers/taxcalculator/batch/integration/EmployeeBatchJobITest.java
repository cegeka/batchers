package be.cegeka.batchers.taxcalculator.batch.integration;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.domain.EmployeeBuilder;
import be.cegeka.batchers.taxcalculator.application.domain.EmployeeRepository;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.springframework.batch.core.BatchStatus.COMPLETED;


public class EmployeeBatchJobITest extends AbstractIntegrationTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    public void jobLaunched_NoEmployees_EmployeeRepositoryIsCalled_NoInteractionWithTheTaxCalculatorService() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        assertThat(jobExecution.getStatus()).isEqualTo(COMPLETED);
    }

    @Test
    public void jobLaunched_oneEmployee_taxIsCalculated() throws Exception {
        Employee employee = new EmployeeBuilder()
                .withFirstName("Monica")
                .withIncome(1000)
                .build();

        employeeRepository.save(employee);

        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        assertThat(jobExecution.getStatus()).isEqualTo(COMPLETED);

        Employee reloadedEmployee = employeeRepository.getBy(employee.getId());
        assertThat(reloadedEmployee.getTaxTotal()).isEqualTo(Money.of(CurrencyUnit.EUR, 100));
        assertThat(reloadedEmployee.getCalculationDate()).isEqualTo(DateTime.now());
    }
}
