package be.cegeka.batchers.taxcalculator.batch;

import be.cegeka.batchers.taxcalculator.domain.Employee;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@ComponentScan(basePackages = "be.cegeka.batchers.taxcalculator.batch")
public class EmployeeJobConfig {

    @Autowired
    private JobBuilderFactory jobBuilders;

    @Autowired
    private StepBuilderFactory stepBuilders;

    @Autowired
    private EmployeeReader reader;

    @Autowired
    private EmployeeProcessor processor;

    @Autowired
    private EmployeeWriter writer;

    @Bean
    public DataSource dataSource(){
        EmbeddedDatabaseBuilder embeddedDatabaseBuilder = new EmbeddedDatabaseBuilder();
        return embeddedDatabaseBuilder
                .addScript("classpath:org/springframework/batch/core/schema-drop-hsqldb.sql")
                .addScript("classpath:org/springframework/batch/core/schema-hsqldb.sql")
                .setType(EmbeddedDatabaseType.HSQL)
                .build();
    }

    @Bean
    public Job employeeJob(){
        return jobBuilders.get("employeeJob")
                .start(step())
                .build();
    }

    @Bean
    public Step step(){
        return stepBuilders.get("step")
                .<Employee,Employee>chunk(1)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
