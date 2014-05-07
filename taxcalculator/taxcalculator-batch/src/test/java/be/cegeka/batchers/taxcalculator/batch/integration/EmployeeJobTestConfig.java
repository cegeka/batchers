package be.cegeka.batchers.taxcalculator.batch.integration;

import be.cegeka.batchers.taxcalculator.domain.EmployeeRepository;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;

import javax.persistence.EntityManagerFactory;

import static org.mockito.Mockito.mock;

@ContextConfiguration
public class EmployeeJobTestConfig {

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private EntityManagerFactory entityManagerFactory;

    @Bean
    public JobLauncherTestUtils jobLauncherTestUtils() {
        return new JobLauncherTestUtils();
    }

    @Bean
    public EmployeeRepository employeeRepository() {
        if(employeeRepository == null) {
            employeeRepository = mock(EmployeeRepository.class);
        }
        return employeeRepository;
    }

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        if(entityManagerFactory == null) {
            entityManagerFactory = mock(EntityManagerFactory.class);
        }
        return entityManagerFactory;
    }

}
