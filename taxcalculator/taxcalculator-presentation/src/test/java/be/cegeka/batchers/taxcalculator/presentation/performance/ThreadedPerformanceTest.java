package be.cegeka.batchers.taxcalculator.presentation.performance;

import be.cegeka.batchers.taxcalculator.application.infrastructure.SmtpServerStub;
import be.cegeka.batchers.taxcalculator.application.service.EmployeeGeneratorService;
import be.cegeka.batchers.taxcalculator.application.service.RunningTimeService;
import be.cegeka.batchers.taxcalculator.batch.integration.AbstractBatchIntegrationTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class ThreadedPerformanceTest extends AbstractBatchIntegrationTest {
    private static final Long NUMBER_OF_EMPLOYEES = 100L;
    private static final String STATUS_OK = "{\"status\": \"OK\" }";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private String taxServiceUrl;
    @Autowired
    private RunningTimeService runningTimeService;
    @Autowired
    private EmployeeGeneratorService employeeGeneratorService;

    @Autowired
    private Job job;
    @Autowired
    private JobRepository jobRepository;

    private JobParameters jobParams;
    private MockRestServiceServer mockServer;
    private SimpleJobLauncher jobLauncher;
    private JobExecution jobExecution;


    @Before
    public void setUp() throws Exception {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        SmtpServerStub.start();
        jobParams = new JobParametersBuilder()
                .addLong("month", 2L)
                .addLong("year", 2014L).toJobParameters();

        employeeGeneratorService.resetEmployees(NUMBER_OF_EMPLOYEES);
        runningTimeService.setMaximumTime(51);
        runningTimeService.setMinimumTime(50);

        jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
    }

    @After
    public void tearDown() throws Exception {
        SmtpServerStub.stop();
    }

    @Test
    public void testSingleThreaded() throws Exception {
        respondSuccess(NUMBER_OF_EMPLOYEES);

        setExecutor(new SyncTaskExecutor());
        jobExecution = jobLauncher.run(job, jobParams);
        printExecutionTime("SingleThreaded", jobExecution);
    }

    private void printExecutionTime(String setup, JobExecution jobExecution) {
        long duration = jobExecution.getEndTime().getTime() - jobExecution.getStartTime().getTime();
        System.out.println("Execution duration for setup [ " + setup + " ] " + duration + " ms");
    }

    private void respondSuccess(long noOfTimes) {
        for (int i = 0; i < noOfTimes; i++) {
            mockServer.expect(requestTo(taxServiceUrl))
                    .andExpect(method(HttpMethod.POST))
                    .andRespond(withSuccess(STATUS_OK, MediaType.APPLICATION_JSON));
        }
    }

    private void setExecutor(TaskExecutor taskExecutor) {
        jobLauncher.setTaskExecutor(taskExecutor);
    }
}
