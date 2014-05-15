package be.cegeka.batchers.taxcalculator.batch;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.infrastructure.config.PersistenceConfig;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import java.util.Arrays;

@Configuration
@EnableBatchProcessing
@ComponentScan(basePackages = "be.cegeka.batchers.taxcalculator.batch")
public class EmployeeJobConfig extends DefaultBatchConfigurer {

    @Autowired
    private JobBuilderFactory jobBuilders;
    @Autowired
    private StepBuilderFactory stepBuilders;
    @Autowired
    private CalculateTaxProcessor calculateTaxProcessor;
    @Autowired
    private CallWebserviceProcessor callWebserviceProcessor;
    @Autowired
    private SendPaycheckProcessor sendPaycheckProcessor;
    @Autowired
    private PersistenceConfig persistenceConfig;

    @Bean
    public JpaPagingItemReader<Employee> employeeItemReader() {
        JpaPagingItemReader<Employee> employeeItemReader = new JpaPagingItemReader<>();
        employeeItemReader.setEntityManagerFactory(persistenceConfig.entityManagerFactory());
        employeeItemReader.setQueryString(Employee.GET_ALL_QUERY);
        return employeeItemReader;
    }

    @Bean
    public JpaItemWriter<Employee> employeeItemWriter() {
        JpaItemWriter<Employee> employeeJpaItemWriter = new JpaItemWriter<>();
        employeeJpaItemWriter.setEntityManagerFactory(persistenceConfig.entityManagerFactory());
        return employeeJpaItemWriter;
    }

    @Bean
    public Job employeeJob() {
        return jobBuilders.get("employeeJob")
                .start(step())
                .build();
    }

    @Bean
    public CompositeItemProcessor<Employee, Employee> processor() {
        CompositeItemProcessor<Employee, Employee> employeeEmployeeCompositeItemProcessor = new CompositeItemProcessor<>();
        employeeEmployeeCompositeItemProcessor.setDelegates(Arrays.asList(
                calculateTaxProcessor,
                callWebserviceProcessor,
                sendPaycheckProcessor
        ));
        return employeeEmployeeCompositeItemProcessor;
    }

    @Bean
    public Step step() {
        return stepBuilders.get("step")
                .<Employee, Employee>chunk(10)
                .reader(employeeItemReader())
                .processor(processor())
                .writer(employeeItemWriter())
                .build();
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer() {
        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(persistenceConfig.dataSource());
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
}
