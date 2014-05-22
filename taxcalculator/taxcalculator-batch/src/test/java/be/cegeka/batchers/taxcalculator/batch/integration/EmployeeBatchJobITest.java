package be.cegeka.batchers.taxcalculator.batch.integration;

import be.cegeka.batchers.taxcalculator.application.ApplicationAssertions;
import be.cegeka.batchers.taxcalculator.application.domain.*;
import be.cegeka.batchers.taxcalculator.application.domain.email.EmailSender;
import be.cegeka.batchers.taxcalculator.application.domain.email.SmtpServerStub;
import be.cegeka.batchers.taxcalculator.batch.CallWebserviceProcessor;
import be.cegeka.batchers.taxcalculator.batch.service.reporting.SumOfTaxes;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

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
    SumOfTaxes sumOfTaxes;
    @Autowired
    CallWebserviceProcessor callWebserviceProcessor;
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private EmailSender emailSender;

    @Autowired
    PayCheckRepository payCheckRepository;

    @Autowired
    TaxCalculationRepository taxCalculationRepository;

    private MockRestServiceServer mockServer;
    private JobParameters jobParams;
    private static Long counter = 0L;

    @Before
    public void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        SmtpServerStub.start();

        Map<String, JobParameter> jobParamsMap = new HashMap<>();
        jobParamsMap.put("month", new JobParameter(Long.valueOf(1), false));
        jobParamsMap.put("year", new JobParameter(Long.valueOf(2014), false));
        jobParamsMap.put("job-id-just-for-testing-shit-up", new JobParameter(counter++, true));

        jobParams = new JobParameters(jobParamsMap);

        Whitebox.setInternalState(emailSender, "emailSendCounter", 0);
    }

    @After
    public void tearDown() {
        SmtpServerStub.stop();
        payCheckRepository.deleteAll();
        taxCalculationRepository.deleteAll();
        employeeRepository.deleteAll();
    }

    @Test
    public void jobLaunched_NoEmployees_EmployeeRepositoryIsCalled_NoInteractionWithTheTaxCalculatorService() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParams);

        assertThat(jobExecution.getStatus()).isEqualTo(COMPLETED);
    }

    @Test
    public void jobLaunched_oneEmployee_taxIsCalculatedAndWebserviceIsCalled() throws Exception {
        Employee employee = haveOneEmployee();

        respondOneTimeWithSuccess();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParams);
        System.out.println(jobExecution.getAllFailureExceptions());
        assertThat(jobExecution.getStatus()).isEqualTo(COMPLETED);

        Employee reloadedEmployee = employeeRepository.getBy(employee.getId());
        System.out.println("RELOADDED: " + reloadedEmployee);

        mockServer.verify();
    }

    @Test
    @Ignore("job won't fail when the call to web service is failing")
    public void jobFailsWhenWebserviceResponseFails() throws Exception {
        haveOneEmployee();

        respondOneTimeWithBadRequest();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParams);
        assertThat(jobExecution.getStatus().isUnsuccessful()).isTrue();
        mockServer.verify();
    }

    @Test
    @Ignore("job won't fail when the call to web service is failing")
    public void jobFailsWhenTwoEmployeesAndOneWebserviceResponseFails() throws Exception {
        haveOneEmployee();
        haveOneEmployee();

        respondOneTimeWithSuccess();
        respondOneTimeWithBadRequest();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParams);
        assertThat(jobExecution.getStatus().isUnsuccessful()).isTrue();
        mockServer.verify();
    }

    @Test
    public void jobRetriesIfWebserviceFails() throws Exception {
        haveOneEmployee();

        mockServer.expect(requestTo(taxServiceUrl)).andExpect(method(HttpMethod.POST))
                .andRespond(withServerError());
        respondOneTimeWithSuccess();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParams);
        assertThat(jobExecution.getStatus()).isEqualTo(COMPLETED);
        mockServer.verify();
    }

    @Test
    public void whenTaxServiceReturnsSuccess_thenPaycheckIsSent() throws Exception {
        haveOneEmployee();
        respondOneTimeWithSuccess();

        jobLauncherTestUtils.launchJob(jobParams);

        ApplicationAssertions.assertThat(SmtpServerStub.wiser()).hasReceivedMessageSentTo(EMAIL_ADDRESS);
    }

    @Test
    public void whenTaxServiceReturnsFail_thenPaycheckIsNotSent() throws Exception {
        haveOneEmployee();
        respondOneTimeWithBadRequest();

        jobLauncherTestUtils.launchJob(jobParams);

        ApplicationAssertions.assertThat(SmtpServerStub.wiser()).hasNoReceivedMessages();
    }

    @Test
    public void testSumOfSuccessTaxesIsCalculated() throws Exception {
        haveOneEmployee();
        haveOneEmployee();

        respondOneTimeWithSuccess();
        respondOneTimeWithSuccess();

        jobLauncherTestUtils.launchJob(jobParams);

        assertThat(sumOfTaxes.getSuccessSum()).isEqualTo(200D);
    }

    @Test
    public void whenWebServiceFailsForOneEmployee_thenSumOfTaxes_isCalculatedOnlyForSuccessfulCalls() throws Exception {
        Whitebox.setInternalState(callWebserviceProcessor, "maxAtempts", 1);

        haveOneEmployee();
        haveOneEmployee();
        haveOneEmployee();

        respondOneTimeWithBadRequest();
        respondOneTimeWithSuccess();
        respondOneTimeWithSuccess();

        Map<String, JobParameter> jobParamsMap = new HashMap<>();
        jobParamsMap.put("month", new JobParameter(Long.valueOf(1), false));
        jobParamsMap.put("year", new JobParameter(Long.valueOf(2014), false));

        JobParameters jobParams = new JobParameters(jobParamsMap);

        jobLauncherTestUtils.launchJob(jobParams);

        assertThat(sumOfTaxes.getSuccessSum()).isEqualTo(200D);
        Whitebox.setInternalState(callWebserviceProcessor, "maxAtempts", 3);
    }

    @Test
    public void whenWebServiceFailsForOneEmployee_thenSumOfTaxes_isCalculatedForFailedCalls() throws Exception {
        Whitebox.setInternalState(callWebserviceProcessor, "maxAtempts", 1);
        haveOneEmployee();
        haveOneEmployee();
        haveOneEmployee();

        respondOneTimeWithBadRequest();
        respondOneTimeWithSuccess();
        respondOneTimeWithSuccess();

        jobLauncherTestUtils.launchJob(jobParams);

        assertThat(sumOfTaxes.getFailedSum()).isEqualTo(100D);
        Whitebox.setInternalState(callWebserviceProcessor, "maxAtempts", 3);
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
        mockServer.expect(requestTo(taxServiceUrl))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(STATUS_OK, MediaType.APPLICATION_JSON));
    }

    private void respondOneTimeWithBadRequest() {
        mockServer.expect(requestTo(taxServiceUrl))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withBadRequest());
    }

}