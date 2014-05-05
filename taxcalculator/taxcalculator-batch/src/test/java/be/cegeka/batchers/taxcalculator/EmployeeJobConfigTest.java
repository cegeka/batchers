package be.cegeka.batchers.taxcalculator;

import be.cegeka.batchers.taxcalculator.domain.Employee;
import be.cegeka.batchers.taxcalculator.domain.EmployeeRepository;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.batch.runtime.BatchStatus;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import java.util.ArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {EmployeeJobConfig.class, JobLauncherTestUtils.class})
@Ignore
public class EmployeeJobConfigTest {

    @Mock
    EmployeeReader employeeReader;

    @Mock
    EmployeeRepository employeeRepository;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    EmployeeJobConfig employeeJobConfig;

    private ArrayList<Employee> employees;

    private Job job;

    @Before
    public void setUp() {
        employeeReader = new EmployeeReader();
        employees = new ArrayList();
        employees.add(new Employee());

        employeeRepository = mock(EmployeeRepository.class);
        when(employeeRepository.getAll()).thenReturn(employees);
        employeeReader.setEmployeeRepo(employeeRepository);
        employeeJobConfig.setReader(employeeReader);

        job = employeeJobConfig.employeeJob();
        jobLauncherTestUtils.setJob(job);
        System.out.println(job);
    }

    @Test
    @Ignore
    public void launchJob() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

    }

}
