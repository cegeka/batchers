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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;

@Configuration
@EnableBatchProcessing
@ComponentScan(basePackages = "be.cegeka.batchers.taxcalculator.batch")
public class EmployeeJobConfig {

    @Autowired
    private JobBuilderFactory jobBuilders;

    @Autowired
    private StepBuilderFactory stepBuilders;

    @Autowired
    private EmployeeProcessor processor;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    protected JobRepository repository;

    @Autowired
    TaskExecutor taskExecutor;

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
    public Job employeeJob(){
        return jobBuilders.get("employeeJob")
                .start(step())
                .build();
    }

    @Bean
    public SimpleJobLauncher jobLauncher(){
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(repository);
        jobLauncher.setTaskExecutor(taskExecutor);

        return  jobLauncher;
    }

    @Bean
    public Step step(){
        return stepBuilders.get("step")
                .<Employee,Employee>chunk(1)
                .reader(employeeItemReader())
                .processor(processor)
                .writer(employeeItemWriter())
                .build();
    }
}
