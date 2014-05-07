package be.cegeka.batchers.taxcalculator.batch.integration;

import be.cegeka.batchers.taxcalculator.domain.Employee;
import be.cegeka.batchers.taxcalculator.domain.EmployeeBuilder;
import be.cegeka.batchers.taxcalculator.domain.EmployeeRepository;
import be.cegeka.batchers.taxcalculator.service.TaxCalculatorService;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.batch.core.BatchStatus.COMPLETED;


public class EmployeeBatchJobTest extends AbstractIntegrationTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private TaxCalculatorService taxCalculatorService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    @Ignore
    public void jobLaunched_NoEmployees_EmployeeRepositoryIsCalled_NoInteractionWithTheTaxCalculatorService() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        assertThat(jobExecution.getStatus()).isEqualTo(COMPLETED);
        verifyZeroInteractions(taxCalculatorService);
    }

    @Test
    @Ignore
    public void jobLaunched_oneEmployee_taxIsCalculated() throws Exception {
        Employee employee = new EmployeeBuilder()
                .withFirstName("Monica")
                .withIncome(1000)
                .build();

        employeeRepository.save(employee);

        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        assertThat(jobExecution.getStatus()).isEqualTo(COMPLETED);

        verify(taxCalculatorService, times(1)).calculateTax(employee);

    }
}
