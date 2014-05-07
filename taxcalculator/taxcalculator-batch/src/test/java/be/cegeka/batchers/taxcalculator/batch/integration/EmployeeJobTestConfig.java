package be.cegeka.batchers.taxcalculator.batch.integration;

import be.cegeka.batchers.taxcalculator.domain.EmployeeRepository;
import be.cegeka.batchers.taxcalculator.service.RunningTimeService;
import be.cegeka.batchers.taxcalculator.service.TaxCalculatorService;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ContextConfiguration;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import static org.mockito.Mockito.mock;

@Configuration
public class EmployeeJobTestConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public DataSourceInitializer dataSourceInitializer() {
        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(dataSource);
        dataSourceInitializer.setDatabasePopulator(dataSourcePopulator());
        return dataSourceInitializer;
    }

    private DatabasePopulator dataSourcePopulator() {
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        databasePopulator.setScripts(
            new ClassPathResource("org/springframework/batch/core/schema-drop-hsqldb.sql"),
            new ClassPathResource("org/springframework/batch/core/schema-hsqldb.sql")
        );
        return databasePopulator;
    }

    @Bean
    public JobLauncherTestUtils jobLauncherTestUtils() {
        return new JobLauncherTestUtils();
    }

    @Primary
    @Bean
    public TaxCalculatorService taxCalculatorService() {
        return mock(TaxCalculatorService.class);
    }

    @Bean
    public RunningTimeService runningTimeService() {
        return  mock(RunningTimeService.class);
    }

    @Bean
    public MockResetter mockResetter() {
        return new MockResetter();
    }

}
