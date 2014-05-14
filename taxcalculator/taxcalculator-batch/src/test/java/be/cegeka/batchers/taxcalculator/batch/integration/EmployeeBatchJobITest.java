package be.cegeka.batchers.taxcalculator.batch.integration;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.domain.EmployeeBuilder;
import be.cegeka.batchers.taxcalculator.application.domain.EmployeeRepository;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.springframework.batch.core.BatchStatus.COMPLETED;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


public class EmployeeBatchJobITest extends AbstractIntegrationTest {
    public static final String STATUS_OK = "{\"status\": \"OK\" }";
    @Autowired
    String taxServiceUrl;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job employeeJob;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @Before
    public void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @After
    public void tearDown() {
        employeeRepository.deleteAll();
    }

    @Test
    public void jobLaunched_NoEmployees_EmployeeRepositoryIsCalled_NoInteractionWithTheTaxCalculatorService() throws Exception {
        JobExecution jobExecution = jobLauncher.run(employeeJob, new JobParameters());

        assertThat(jobExecution.getStatus()).isEqualTo(COMPLETED);
    }

//    @Test
//    public void jobLaunched_oneEmployee_taxIsCalculatedAndWebserviceIsCalled() throws Exception {
//        Employee employee = haveOneEmployee();
//
//        mockServer.expect(requestTo(taxServiceUrl)).andExpect(method(HttpMethod.POST))
//                .andRespond(withSuccess(STATUS_OK, MediaType.APPLICATION_JSON));
//
//        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
//        System.out.println(jobExecution.getAllFailureExceptions());
//        assertThat(jobExecution.getStatus()).isEqualTo(COMPLETED);
//
//        Employee reloadedEmployee = employeeRepository.getBy(employee.getId());
//        assertThat(reloadedEmployee.getTaxTotal()).isEqualTo(Money.of(CurrencyUnit.EUR, 100));
//        assertThat(reloadedEmployee.getCalculationDate()).isEqualTo(DateTime.now());
//
//        mockServer.verify();
//    }
//
//    @Test
//    public void jobFailsWhenWebserviceResponseFails() throws Exception {
//        haveOneEmployee();
//
//        mockServer.expect(requestTo(taxServiceUrl)).andExpect(method(HttpMethod.POST))
//                .andRespond(withBadRequest());
//
//        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
//        assertThat(jobExecution.getStatus().isUnsuccessful()).isTrue();
//        mockServer.verify();
//    }
//
//    @Test
//    public void jobFailsWhenTwoEmployeesAndOneWebserviceResponseFails() throws Exception {
//        haveOneEmployee();
//        haveOneEmployee();
//
//        mockServer.expect(requestTo(taxServiceUrl)).andExpect(method(HttpMethod.POST))
//                .andRespond(withSuccess(STATUS_OK, MediaType.APPLICATION_JSON));
//        mockServer.expect(requestTo(taxServiceUrl)).andExpect(method(HttpMethod.POST))
//                .andRespond(withBadRequest());
//
//        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
//        assertThat(jobExecution.getStatus().isUnsuccessful()).isTrue();
//        mockServer.verify();
//    }
//
//    @Test
//    public void jobRetriesIfWebserviceFails() throws Exception {
//        haveOneEmployee();
//
//        mockServer.expect(requestTo(taxServiceUrl)).andExpect(method(HttpMethod.POST))
//                .andRespond(withServerError());
//        mockServer.expect(requestTo(taxServiceUrl)).andExpect(method(HttpMethod.POST))
//                .andRespond(withSuccess(STATUS_OK, MediaType.APPLICATION_JSON));
//
//        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
//        assertThat(jobExecution.getStatus()).isEqualTo(COMPLETED);
//        mockServer.verify();
//    }

    private Employee haveOneEmployee() {
        Employee employee = new EmployeeBuilder()
                .withFirstName("Monica")
                .withIncome(1000)
                .build();

        employeeRepository.save(employee);
        return employee;
    }

}
