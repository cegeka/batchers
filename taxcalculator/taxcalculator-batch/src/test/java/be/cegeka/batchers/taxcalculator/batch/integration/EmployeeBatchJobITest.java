package be.cegeka.batchers.taxcalculator.batch.integration;

import be.cegeka.batchers.taxcalculator.application.ApplicationAssertions;
import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.domain.EmployeeBuilder;
import be.cegeka.batchers.taxcalculator.application.domain.EmployeeRepository;
import be.cegeka.batchers.taxcalculator.application.domain.email.SmtpServerStub;
import be.cegeka.batchers.taxcalculator.batch.config.SumOfTaxes;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
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
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;


public class EmployeeBatchJobITest extends AbstractIntegrationTest {
    public static final String STATUS_OK = "{\"status\": \"OK\" }";
    public static final String EMAIL_ADDRESS = "employee@email.com";

    @Autowired
    String taxServiceUrl;
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    SumOfTaxes sumOfTaxes;

    private MockRestServiceServer mockServer;

    @Before
    public void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        SmtpServerStub.start();
    }

    @After
    public void tearDown() {
        employeeRepository.deleteAll();
        SmtpServerStub.stop();
    }

    @Test
    public void jobLaunched_NoEmployees_EmployeeRepositoryIsCalled_NoInteractionWithTheTaxCalculatorService() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        assertThat(jobExecution.getStatus()).isEqualTo(COMPLETED);
    }

    @Test
    public void jobLaunched_oneEmployee_taxIsCalculatedAndWebserviceIsCalled() throws Exception {
        Employee employee = haveOneEmployee();

        respondOneTimeWithSuccess();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        System.out.println(jobExecution.getAllFailureExceptions());
        assertThat(jobExecution.getStatus()).isEqualTo(COMPLETED);

        Employee reloadedEmployee = employeeRepository.getBy(employee.getId());
        System.out.println("RELOADDED: " + reloadedEmployee);
        assertThat(reloadedEmployee.getTaxTotal()).isEqualTo(Money.of(CurrencyUnit.EUR, 100));
        assertThat(reloadedEmployee.getCalculationDate()).isEqualTo(DateTime.now());

        mockServer.verify();
    }

    @Test
    public void jobFailsWhenWebserviceResponseFails() throws Exception {
        haveOneEmployee();

        respondOneTimeWithBadRequest();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        assertThat(jobExecution.getStatus().isUnsuccessful()).isTrue();
        mockServer.verify();
    }

    @Test
    public void jobFailsWhenTwoEmployeesAndOneWebserviceResponseFails() throws Exception {
        haveOneEmployee();
        haveOneEmployee();

        respondOneTimeWithSuccess();
        respondOneTimeWithBadRequest();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        assertThat(jobExecution.getStatus().isUnsuccessful()).isTrue();
        mockServer.verify();
    }

    @Test
    public void jobRetriesIfWebserviceFails() throws Exception {
        haveOneEmployee();

        mockServer.expect(requestTo(taxServiceUrl)).andExpect(method(HttpMethod.POST))
                .andRespond(withServerError());
        respondOneTimeWithSuccess();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        assertThat(jobExecution.getStatus()).isEqualTo(COMPLETED);
        mockServer.verify();
    }

    @Test
    public void whenTaxServiceReturnsSuccess_thenPaycheckIsSent() throws Exception {
        haveOneEmployee();
        respondOneTimeWithSuccess();

        jobLauncherTestUtils.launchJob();

        ApplicationAssertions.assertThat(SmtpServerStub.wiser()).hasReceivedMessageSentTo(EMAIL_ADDRESS);
    }

    @Test
    public void whenTaxServiceReturnsFail_thenPaycheckIsNotSent() throws Exception {
        haveOneEmployee();
        respondOneTimeWithBadRequest();

        jobLauncherTestUtils.launchJob();

        ApplicationAssertions.assertThat(SmtpServerStub.wiser()).hasNoReceivedMessages();
    }

    @Test
    public void testSumOfSuccessTaxesIsCalculated() throws Exception {
        haveOneEmployee();
        haveOneEmployee();

        respondOneTimeWithSuccess();
        respondOneTimeWithSuccess();

        jobLauncherTestUtils.launchJob();

        assertThat(sumOfTaxes.getSuccessSum()).isEqualTo(200D);
    }

    @Test
    public void whenWebServiceFailsForOneEmployee_thenSumOfTaxes_isCalculatedOnlyForSuccessfulCalls() throws Exception {
        haveOneEmployee();
        haveOneEmployee();
        haveOneEmployee();

        respondOneTimeWithSuccess();
        respondOneTimeWithSuccess();
        respondOneTimeWithBadRequest();

        jobLauncherTestUtils.launchJob();

        assertThat(sumOfTaxes.getSuccessSum()).isEqualTo(200D);
    }

    private Employee haveOneEmployee() {
        Employee employee = new EmployeeBuilder()
                .withFirstName("Monica")
                .withLastName("Dev")
                .withIncome(1000)
                .withEmailAddress(EMAIL_ADDRESS)
                .build();

        employeeRepository.save(employee);
        return employee;
    }

    private void respondOneTimeWithSuccess() {
        mockServer.expect(requestTo(taxServiceUrl)).andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(STATUS_OK, MediaType.APPLICATION_JSON));
    }

    private void respondOneTimeWithBadRequest() {
        mockServer.expect(requestTo(taxServiceUrl)).andExpect(method(HttpMethod.POST))
                .andRespond(withBadRequest());
    }

}