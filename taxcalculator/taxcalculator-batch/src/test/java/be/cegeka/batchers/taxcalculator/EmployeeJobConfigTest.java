package be.cegeka.batchers.taxcalculator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.batch.runtime.BatchStatus;

import static org.testng.AssertJUnit.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {EmployeeJobConfig.class, JobLauncherTestUtils.class})
public class EmployeeJobConfigTest {


    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Resource(name="employeeJob")
    private Job job;

    @Test
    public void launchJob() throws Exception {
        System.out.println(job);

        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

    }

}
