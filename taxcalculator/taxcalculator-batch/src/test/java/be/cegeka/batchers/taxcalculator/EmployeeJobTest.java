package be.cegeka.batchers.taxcalculator;

import be.cegeka.batchers.taxcalculator.domain.Employee;
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
import org.springframework.test.context.web.WebAppConfiguration;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Date;

import static org.junit.Assert.assertTrue;

@ContextConfiguration("employeeJobTest-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class EmployeeJobTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    public void testEmployeeJob() throws Exception {
        JobExecution jobExecution = jobLauncher.run(job, new JobParametersBuilder()
                .addDate("run.date", new Date())
                .toJobParameters());

        assertTrue("Batch status must be COMPLETED", jobExecution.getStatus() == BatchStatus.COMPLETED);

        CriteriaBuilder qb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = qb.createQuery(Long.class);
        cq.select(qb.count(cq.from(Employee.class)));
        Long numRows = entityManager.createQuery(cq).getSingleResult();

        assertTrue("Invalid number of rows found, expected 300 got: " + numRows, numRows == 300);

        TypedQuery<Employee> query =
                entityManager.createQuery("SELECT e FROM Employee e", Employee.class).setMaxResults(1);
        Employee employee = query.getSingleResult();

        assertTrue("Employee has name not upeprcase " + employee.getLastName(),
                employee.getLastName().toUpperCase().equals(employee.getLastName()));

    }
}
