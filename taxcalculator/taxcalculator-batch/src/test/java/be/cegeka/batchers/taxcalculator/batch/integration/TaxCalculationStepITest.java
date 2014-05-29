package be.cegeka.batchers.taxcalculator.batch.integration;

import be.cegeka.batchers.taxcalculator.application.domain.*;
import be.cegeka.batchers.taxcalculator.batch.config.EmployeeJobConfig;
import org.junit.After;
import org.junit.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class TaxCalculationStepITest extends AbstractIntegrationTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private TaxCalculationRepository taxCalculationRepository;

    @After
    public void tearDown() {
        taxCalculationRepository.truncate();
        employeeRepository.truncate();
    }

    @Test
    public void taxCalculationStep_noWork() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("year", 2014L, true)
                .addLong("month", 5L, true)
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchStep(EmployeeJobConfig.TAX_CALCULATION_STEP, jobParameters);

        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        assertThat(taxCalculationRepository.findByYearAndMonth(2014, 5)).isEmpty();
    }

    @Test
    public void taxCalculationStep_generatesCorrectCalculation() throws Exception {
        Employee employee = haveOneEmployee();

        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("year", 2014L, true)
                .addLong("month", 5L, true)
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchStep(EmployeeJobConfig.TAX_CALCULATION_STEP, jobParameters);

        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

        List<TaxCalculation> byEmployee = taxCalculationRepository.findByEmployee(employee);

        assertThat(byEmployee).hasSize(1);
        TaxCalculation taxCalculation = byEmployee.get(0);
        assertThat(taxCalculation.getEmployee().getId()).isEqualTo(employee.getId());
        assertThat(taxCalculation.getYear()).isEqualTo(2014);
        assertThat(taxCalculation.getMonth()).isEqualTo(5);

        List<TaxCalculation> byYearAndMonth = taxCalculationRepository.findByYearAndMonth(2014, 5);
        assertThat(byYearAndMonth).hasSize(1);
    }

    private Employee haveOneEmployee() {
        Employee employee = new EmployeeBuilder()
                .withFirstName("Monica")
                .withLastName("Dev")
                .withIncome(1000)
                .withEmailAddress("monica@cegeka.com")
                .build();

        employeeRepository.save(employee);
        return employee;
    }

}