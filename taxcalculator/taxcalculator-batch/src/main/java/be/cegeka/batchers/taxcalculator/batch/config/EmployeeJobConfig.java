package be.cegeka.batchers.taxcalculator.batch.config;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.infrastructure.config.PropertyPlaceHolderConfig;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;

@Configuration
@EnableBatchProcessing
@ComponentScan(basePackages = "be.cegeka.batchers.taxcalculator.batch")
@Import({PropertyPlaceHolderConfig.class, TempConfigToInitDB.class, ItemReaderWriterConfig.class, ProcessorConfig.class})
@PropertySource("classpath:taxcalculator-batch.properties")
public class EmployeeJobConfig extends DefaultBatchConfigurer {

    @Autowired
    private JobBuilderFactory jobBuilders;
    @Autowired
    private StepBuilderFactory stepBuilders;

    @Autowired
    private ProcessorConfig processorConfig;
    @Autowired
    private ItemReaderWriterConfig itemReaderWriterConfig;


    @Bean
    public Job employeeJob() {
        return jobBuilders.get("employeeJob")
                .start(step())
                .build();
    }


    @Bean
    public Step step() {
        return stepBuilders.get("step")
                .<Employee, Employee>chunk(1)
                .reader(itemReaderWriterConfig.employeeItemReader())
                .processor(processorConfig.processor())
                .writer(itemReaderWriterConfig.employeeItemWriter())
                .build();
    }

}


