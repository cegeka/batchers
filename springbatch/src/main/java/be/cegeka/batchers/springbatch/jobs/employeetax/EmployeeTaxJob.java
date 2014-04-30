package be.cegeka.batchers.springbatch.jobs.employeetax;

import be.cegeka.batchers.springbatch.domain.Employee;
import be.cegeka.batchers.springbatch.jobs.StandaloneInfrastructureConfiguration;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by andreip on 29.04.2014.
 */
@Configuration
@Import(StandaloneInfrastructureConfiguration.class)
public class EmployeeTaxJob {

    @Autowired
    private JobBuilderFactory jobBuilders;

    @Autowired
    private StepBuilderFactory stepBuilders;

/*
    @Autowired
    private EmployeeReader employeeReader;

    @Autowired
    private EmployeeWriter employeeWriter;

    @Autowired
    private EmployeeProcessor employeeProcessor;
*/

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
                .reader(getEmployeeReader())
                .processor(getEmployeeProcessor())
                .writer(getEmployeeWriter())
                .build();
    }

    private EmployeeWriter getEmployeeWriter() {
        return new EmployeeWriter();
    }

    private EmployeeProcessor getEmployeeProcessor() {
        return new EmployeeProcessor();
    }

    private EmployeeReader getEmployeeReader() {
        return new EmployeeReader();
    }

}
