package be.cegeka.batchers.taxcalculator.batch.integration;

import be.cegeka.batchers.taxcalculator.application.domain.*;
import be.cegeka.batchers.taxcalculator.application.domain.email.EmailSender;
import be.cegeka.batchers.taxcalculator.application.domain.email.SmtpServerStub;
import be.cegeka.batchers.taxcalculator.batch.CallWebserviceProcessor;
import be.cegeka.batchers.taxcalculator.batch.config.EmployeeJobConfig;
import be.cegeka.batchers.taxcalculator.batch.service.reporting.SumOfTaxes;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.util.AssertionErrors;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static be.cegeka.batchers.taxcalculator.application.ApplicationAssertions.assertThat;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.springframework.batch.core.BatchStatus.COMPLETED;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

public class EmployeeBatchJobITest extends AbstractIntegrationTest {
    private static final String STATUS_OK = "{\"status\": \"OK\" }";
    private static final String EMAIL_ADDRESS = "employee@email.com";
    private static final Long YEAR = 2014L;
    private static final Long MONTH = 1L;
    private static Long counter = 0L;

    @Autowired
    private EmployeeJobConfig employeeJobConfig;
    @Autowired
    private String taxServiceUrl;
    @Autowired
    private SumOfTaxes sumOfTaxes;
    @Autowired
    private CallWebserviceProcessor callWebserviceProcessor;
    @Autowired
    private PayCheckRepository payCheckRepository;
    @Autowired
    private TaxCalculationRepository taxCalculationRepository;
    @Autowired
    private TaxServiceCallResultRepository taxServiceCallResultRepository;
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private MonthlyReportRepository monthlyReportRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private EmailSender emailSender;

    private MockRestServiceServer mockServer;
    private JobParameters jobParams;

    @Before
    public void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        SmtpServerStub.start();

        Map<String, JobParameter> jobParamsMap = new HashMap<>();
        jobParamsMap.put("month", new JobParameter(MONTH, false));
        jobParamsMap.put("year", new JobParameter(YEAR, false));
        jobParamsMap.put("job-id-just-for-testing-shit-up", new JobParameter(counter++, true));

        jobParams = new JobParameters(jobParamsMap);

        Whitebox.setInternalState(emailSender, "emailSendCounter", 0);
    }

    @After
    public void tearDown() {
        SmtpServerStub.stop();
        monthlyReportRepository.deleteAll();
        payCheckRepository.deleteAll();
        taxServiceCallResultRepository.deleteAll();
        taxCalculationRepository.deleteAll();
        employeeRepository.deleteAll();
    }

    @Test
    public void jobLaunched_NoEmployees_EmployeeRepositoryIsCalled_NoInteractionWithTheTaxCalculatorService() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParams);

        assertThat(jobExecution.getStatus()).isEqualTo(COMPLETED);
        verifyJob(jobExecution);
    }

    @Test
    public void jobFailsWhenWebserviceResponseFailsWithClientException_WeAreMakingABobo() throws Exception {
        haveEmployees(1);
        respondWithBadRequest(1);

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParams);

        assertThat(jobExecution.getStatus().isUnsuccessful()).isTrue();
        verifyJob(jobExecution);
    }

    @Test
    public void jobDoesNotFailWhenWebserviceResponseFails2TimesWithServerException_BackendMakesABobo() throws Exception {
        haveEmployees(1);
        respondWithServerError(2);
        respondOneTimeWithSuccess();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParams);

        assertThat(jobExecution.getStatus().isUnsuccessful()).isFalse();
        verifyJob(jobExecution);
    }

    @Test
    public void jobDoesFailBecauseOfSkipsWhenWebserviceResponseFails3TimesWithServerException_BackendMakesABobo() throws Exception {
        haveEmployees(2);
        respondWithServerError(4);
        respondOneTimeWithSuccess();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParams);

        assertThat(jobExecution.getStatus().isUnsuccessful()).isTrue();
        assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("FAILED-BECAUSE-OF-SKIPS");
        verifyJob(jobExecution);
    }

    @Test
    @Ignore("contact ronald for more info | ronald will try to fix")
    public void jobDoesFailCompletelyWhenWebserviceResponseFails9TimesConsecutiveWithServerException_BackendMakesABobo() throws Exception {
        Whitebox.setInternalState(employeeJobConfig, "maxConsecutiveTaxWebServiceExceptions", 3);
        haveEmployees(4);
        respondWithServerError(9);

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParams);

        assertThat(jobExecution.getStatus().isUnsuccessful()).isTrue();
        assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("FAILED");
        verifyJob(jobExecution);
    }

    @Test
    @Ignore("contact ronald for more info | ronald will try to fix")
    public void jobDoesContinueWhenWebserviceResponseFails8TimesConsecutiveWithServerException_BackendMakesABobo() throws Exception {
        Whitebox.setInternalState(employeeJobConfig, "maxConsecutiveTaxWebServiceExceptions", 3);
        haveEmployees(5);
        respondWithServerError(8); //5->employee 1; 2->employee 2;
        respondOneTimeWithSuccess();//->employee 3
        respondWithServerError(5);//2->employee 4
        respondOneTimeWithSuccess();//->employee 5

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParams);

        assertThat(jobExecution.getStatus().isUnsuccessful()).isTrue();
        assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("FAILED-BECAUSE-OF-SKIPS");
        verifyJob(jobExecution);
    }

    @Test
    public void jobLaunched_oneEmployee_taxIsCalculatedAndWebserviceIsCalled() throws Exception {
        Employee employee = haveOneEmployee();

        respondOneTimeWithSuccess();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParams);
        assertThat(jobExecution.getStatus()).isEqualTo(COMPLETED);

        assertThat(taxCalculationRepository.findByEmployee(employee)).hasSize(1);
        assertThat(taxCalculationRepository.findByEmployee(employee).get(0).getTax().getAmount()).isPositive();

        verifyJob(jobExecution);
    }

    @Test
    public void whenTaxServiceReturnsSuccess_thenPaycheckIsSent() throws Exception {
        haveEmployees(1);
        respondOneTimeWithSuccess();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParams);

        assertThat(SmtpServerStub.wiser()).hasReceivedMessageSentTo(EMAIL_ADDRESS);
        verifyJob(jobExecution);
    }

    @Test
    public void whenTaxServiceReturnsFail_thenPaycheckIsNotSent() throws Exception {
        haveEmployees(1);
        respondWithBadRequest(1);

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParams);

        assertThat(SmtpServerStub.wiser()).hasNoReceivedMessages();
        verifyJob(jobExecution);
    }

    @Test
    public void testSumOfSuccessTaxesIsCalculated() throws Exception {
        haveEmployees(2);

        respondOneTimeWithSuccess();
        respondOneTimeWithSuccess();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParams);

        assertThat(sumOfTaxes.getSuccessSum(YEAR, MONTH)).isEqualTo(200D);
        verifyJob(jobExecution);
    }

    @Test
    public void whenWebServiceFailsForOneEmployee_thenSumOfTaxes_isCalculatedOnlyForSuccessfulCalls() throws Exception {
        haveEmployees(3);

        respondWithServerError(3);
        respondOneTimeWithSuccess();
        respondOneTimeWithSuccess();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParams);

        assertThat(sumOfTaxes.getSuccessSum(YEAR, MONTH)).isEqualTo(200D);
        verifyJob(jobExecution);
    }

    @Test
    public void whenWebServiceFailsForOneEmployee_thenSumOfTaxes_isCalculatedForFailedCalls() throws Exception {
         haveEmployees(3);

        respondWithServerError(3);
        respondOneTimeWithSuccess();
        respondOneTimeWithSuccess();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParams);

        assertThat(sumOfTaxes.getFailedSum(YEAR, MONTH)).isEqualTo(100D);
        verifyJob(jobExecution);
    }

    @Test
    @Ignore("Implement me")
    public void rerunSameMonthJobIfPreviousJobHasFailures() {
        throw new UnsupportedOperationException("Implement me");
    }

    @Test
    @Ignore("Implement me")
    public void rerunSameMonthJobIfPreviousJobHasCompletedSuccessFully() {
        throw new UnsupportedOperationException("Implement me");
    }

    private void verifyJob(JobExecution jobExecution) {
        mockServer.verify();

        jobExecution.getStepExecutions()
                .forEach(se -> se.getFailureExceptions()
                        .forEach(t -> assertThat(t).isNotInstanceOf(AssertionError.class)));
    }

    private void haveEmployees(int nrOfEmployees) {
        for (int i = 0; i < nrOfEmployees; i++) {
            haveOneEmployee();
        }
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

    private void respondWithBadRequest(int nrOfTimes) {
        for (int i = 0; i < nrOfTimes; i++) {
            mockServer.expect(requestTo(taxServiceUrl))
                    .andExpect(method(HttpMethod.POST))
                    .andRespond(withBadRequest());
        }
    }

    private void respondWithServerError(int nrOfTimes) {
        for (int i = 0; i < nrOfTimes; i++) {
            mockServer.expect(requestTo(taxServiceUrl))
                    .andExpect(method(HttpMethod.POST))
                    .andRespond(withServerError());
        }
    }

}