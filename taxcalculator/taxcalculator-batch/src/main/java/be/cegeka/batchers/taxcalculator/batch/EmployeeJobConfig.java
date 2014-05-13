package be.cegeka.batchers.taxcalculator.batch;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import javax.persistence.EntityManagerFactory;
import java.util.Arrays;

@Configuration
@EnableBatchProcessing
@ComponentScan(basePackages = "be.cegeka.batchers.taxcalculator.batch")
public class EmployeeJobConfig {

    @Autowired
    JobRepository repository;
    @Autowired
    private JobBuilderFactory jobBuilders;
    @Autowired
    private StepBuilderFactory stepBuilders;
    @Autowired
    private CalculateTaxProcessor calculateTaxProcessor;
    @Autowired
    private CallWebserviceProcessor callWebserviceProcessor;
    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Bean
    public JpaPagingItemReader<Employee> employeeItemReader() {
        JpaPagingItemReader<Employee> employeeItemReader = new JpaPagingItemReader<>();
        employeeItemReader.setEntityManagerFactory(entityManagerFactory);
        employeeItemReader.setQueryString(Employee.GET_ALL_QUERY);
        return employeeItemReader;
    }

    @Bean
    public JpaItemWriter<Employee> employeeItemWriter() {
        JpaItemWriter<Employee> employeeJpaItemWriter = new JpaItemWriter<>();
        employeeJpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return employeeJpaItemWriter;
    }

    @Bean
    public Job employeeJob() {
        return jobBuilders.get("employeeJob")
                .start(step())
                .build();
    }

    @Bean
    public SimpleJobLauncher jobLauncher() {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(repository);
        TaskExecutor syncTaskExecutor = new SyncTaskExecutor();
        jobLauncher.setTaskExecutor(syncTaskExecutor);
        return jobLauncher;
    }

    @Bean
    public CompositeItemProcessor<Employee, Employee> processor() {
        CompositeItemProcessor<Employee, Employee> employeeEmployeeCompositeItemProcessor = new CompositeItemProcessor<>();
        employeeEmployeeCompositeItemProcessor.setDelegates(Arrays.asList(
                calculateTaxProcessor,
                callWebserviceProcessor
        ));
        return employeeEmployeeCompositeItemProcessor;
    }

    @Bean
    public Step step() {
        return stepBuilders.get("step")
                .<Employee, Employee>chunk(1)
                .reader(employeeItemReader())
                .processor(processor())
                .writer(employeeItemWriter())
                .build();
    }
}
