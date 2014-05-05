package be.cegeka.batchers.taxcalculator;

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
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class EmployeeJobConfig {

    @Autowired
    private JobBuilderFactory jobBuilders;

    @Autowired
    private StepBuilderFactory stepBuilders;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ItemReader<Employee> reader;

    @Bean
    public DataSource dataSource(){
        EmbeddedDatabaseBuilder embeddedDatabaseBuilder = new EmbeddedDatabaseBuilder();
        return embeddedDatabaseBuilder.addScript("classpath:org/springframework/batch/core/schema-drop-hsqldb.sql")
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
                .reader(getReader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    private ItemWriter<Employee> writer() {
        return new EmployeeWriter();
    }

    private ItemProcessor<Employee,Employee> processor() {
        return new EmployeeProcessor();
    }

    private ItemReader<Employee> getReader() {
        if(reader == null){
            reader = new EmployeeReader();
        }
        return reader;
    }

    public void setReader(ItemReader<Employee> reader) {
        this.reader = reader;
    }
}
