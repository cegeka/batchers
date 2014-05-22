package be.cegeka.batchers.taxcalculator.batch.config;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.domain.PayCheck;
import be.cegeka.batchers.taxcalculator.application.domain.TaxCalculation;
import be.cegeka.batchers.taxcalculator.application.domain.TaxServiceCallResult;
import be.cegeka.batchers.taxcalculator.batch.CalculateTaxProcessor;
import be.cegeka.batchers.taxcalculator.batch.CallWebserviceProcessor;
import be.cegeka.batchers.taxcalculator.batch.SendPaycheckProcessor;
import be.cegeka.batchers.taxcalculator.batch.service.reporting.EmployeeJobExecutionListener;
import be.cegeka.batchers.taxcalculator.batch.service.reporting.SumOfTaxesItemListener;
import be.cegeka.batchers.taxcalculator.infrastructure.config.PropertyPlaceHolderConfig;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.builder.FaultTolerantStepBuilder;
import org.springframework.batch.core.step.skip.AlwaysSkipItemSkipPolicy;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;

import java.util.Arrays;

@Configuration
@EnableBatchProcessing
@ComponentScan(basePackages = "be.cegeka.batchers.taxcalculator.batch")
@Import({PropertyPlaceHolderConfig.class, TempConfigToInitDB.class, ItemReaderWriterConfig.class})
@PropertySource("classpath:taxcalculator-batch.properties")
public class EmployeeJobConfig extends DefaultBatchConfigurer {

    @Autowired
    private JobBuilderFactory jobBuilders;

    @Autowired
    private StepBuilderFactory stepBuilders;

    @Autowired
    private ItemReaderWriterConfig itemReaderWriterConfig;

    @Autowired
    private SumOfTaxesItemListener sumOfTaxesItemListener;

    @Autowired
    private EmployeeJobExecutionListener employeeJobExecutionListener;

    @Autowired
    private CalculateTaxProcessor calculateTaxProcessor;

    @Autowired
    private CallWebserviceProcessor callWebserviceProcessor;

    @Autowired
    private SendPaycheckProcessor sendPaycheckProcessor;

    private static Integer OVERRIDDEN_BY_EXPRESSION = null;

    @Bean
    public Job employeeJob() {
        return jobBuilders.get("employeeJob")
                .start(taxCalculationStep())
                .next(wsCallStep())
                .listener(employeeJobExecutionListener)
                .build();
    }

    @Bean
    public Step taxCalculationStep() {
        FaultTolerantStepBuilder<Employee, Employee> faultTolerantStepBuilder = stepBuilders.get("taxCalculationStep")
                .<Employee, Employee>chunk(1)
                .faultTolerant();

        return faultTolerantStepBuilder
                .reader(itemReaderWriterConfig.taxCalculatorItemReader())
                .processor(calculateTaxProcessor)
                .writer(itemReaderWriterConfig.taxCalculatorItemWriter())
                .build();
    }

    @Bean
    public Step wsCallStep() {
        FaultTolerantStepBuilder<TaxCalculation, PayCheck> faultTolerantStepBuilder = stepBuilders.get("wsCallStep")
                .<TaxCalculation, PayCheck>chunk(1)
                .faultTolerant();

        faultTolerantStepBuilder.listener((SkipListener) sumOfTaxesItemListener);
        faultTolerantStepBuilder.skipPolicy(new AlwaysSkipItemSkipPolicy());


        CompositeItemProcessor<TaxCalculation, PayCheck> compositeItemProcessor = new CompositeItemProcessor<>();
        compositeItemProcessor.setDelegates(Arrays.asList(
                callWebserviceProcessor,
                sendPaycheckProcessor
        ));

        return faultTolerantStepBuilder
                .reader(itemReaderWriterConfig.wsCallItemReader(OVERRIDDEN_BY_EXPRESSION, OVERRIDDEN_BY_EXPRESSION))
                .processor(compositeItemProcessor)
                .writer(itemReaderWriterConfig.wsCallItemWriter())
                .listener(sumOfTaxesItemListener)
                .build();
    }

}
