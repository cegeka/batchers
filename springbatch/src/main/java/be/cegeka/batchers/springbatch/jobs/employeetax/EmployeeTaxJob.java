package be.cegeka.batchers.springbatch.jobs.employeetax;

import be.cegeka.batchers.springbatch.domain.Employee;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by andreip on 29.04.2014.
 */
@Configuration
@EnableBatchProcessing
public class EmployeeTaxJob {

    @Autowired
    private JobBuilderFactory jobBuilders;

    @Autowired
    private StepBuilderFactory stepBuilders;

    @Autowired
    private EmployeeReader employeeReader;

    @Autowired
    private EmployeeWriter employeeWriter;

    @Autowired
    private EmployeeProcessor employeeProcessor;

    @Bean
    public Job employeeTaxJob() {
        return jobBuilders.get("employeeTaxJob")
                .start(step())
                .build();
    }

    @Bean
    public Step step() {
        return stepBuilders.get("step")
                .<Employee, Employee>chunk(1)
                .reader(employeeReader)
                .processor(employeeProcessor)
                .writer(employeeWriter)
                .build();
    }

}
