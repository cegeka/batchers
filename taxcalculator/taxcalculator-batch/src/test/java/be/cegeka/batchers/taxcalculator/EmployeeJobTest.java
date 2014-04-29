package be.cegeka.batchers.taxcalculator;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import static org.junit.Assert.assertTrue;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class EmployeeJobTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @Test
    public void testEmployeeJob() throws Exception {
        JobExecution jobExecution = jobLauncher.run(job, new JobParametersBuilder()
                .addDate("run.date", new Date())
                .toJobParameters());

        assertTrue("Batch status must be COMPLETED", jobExecution.getStatus() == BatchStatus.COMPLETED);

        int numRows = jdbcTemplate.queryForObject("select count(*) from people", Integer.class);
        assertTrue("Invalid number of rows found, expected 5 got: " + numRows, numRows == 5);

        int numTransformedRows = jdbcTemplate.queryForObject("select count(*) from people where last_name='DOE'",
                Integer.class);
        assertTrue("Invalid number of transformed rows found, expected 5 got: " + numTransformedRows,
                numTransformedRows == 5);
    }
}
