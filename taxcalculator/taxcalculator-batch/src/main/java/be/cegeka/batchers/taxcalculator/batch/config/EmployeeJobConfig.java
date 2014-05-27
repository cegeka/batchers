package be.cegeka.batchers.taxcalculator.batch.config;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.domain.PayCheck;
import be.cegeka.batchers.taxcalculator.application.domain.TaxCalculation;
import be.cegeka.batchers.taxcalculator.application.service.TaxWebServiceException;
import be.cegeka.batchers.taxcalculator.batch.CalculateTaxProcessor;
import be.cegeka.batchers.taxcalculator.batch.CallWebserviceProcessor;
import be.cegeka.batchers.taxcalculator.batch.SendPaycheckProcessor;
import be.cegeka.batchers.taxcalculator.batch.tasklet.JobResultsTasklet;
import be.cegeka.batchers.taxcalculator.infrastructure.config.PropertyPlaceHolderConfig;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.builder.FaultTolerantStepBuilder;
import org.springframework.batch.core.step.skip.AlwaysSkipItemSkipPolicy;
import org.springframework.batch.item.database.JpaPagingItemReader;
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

    public static final String TAX_CALCULATION_STEP = "taxCalculationStep";
    private static final String EMPLOYEE_JOB = "employeeJob";
    private static final String WS_CALL_STEP = "wsCallStep";
    private static Long OVERRIDDEN_BY_EXPRESSION = null;
    @Autowired
    private JobBuilderFactory jobBuilders;
    @Autowired
    private StepBuilderFactory stepBuilders;
    @Autowired
    private ItemReaderWriterConfig itemReaderWriterConfig;
    @Autowired
    private JpaPagingItemReader<Employee> taxCalculatorItemReader;
    @Autowired
    private CalculateTaxProcessor calculateTaxProcessor;
    @Autowired
    private CallWebserviceProcessor callWebserviceProcessor;
    @Autowired
    private SendPaycheckProcessor sendPaycheckProcessor;
    @Autowired
    private JobResultsTasklet jobResultsTasklet;

    @Bean
    public Job employeeJob() {
        return jobBuilders.get(EMPLOYEE_JOB)
                .start(taxCalculationStep())
                .next(wsCallStep())

                .next(jobResultsPdf())
                .build();
    }

    @Bean
    public Step taxCalculationStep() {
        return stepBuilders
                .get(TAX_CALCULATION_STEP)
                .<Employee, TaxCalculation>chunk(5)
                .reader(taxCalculatorItemReader)
                .processor(calculateTaxProcessor)
                .writer(itemReaderWriterConfig.taxCalculatorItemWriter())
                .build();
    }

    @Bean
    public Step wsCallStep() {
        FaultTolerantStepBuilder<TaxCalculation, PayCheck> faultTolerantStepBuilder = stepBuilders.get(WS_CALL_STEP)
                .<TaxCalculation, PayCheck>chunk(5)
                .faultTolerant();

        faultTolerantStepBuilder.skipPolicy(new AlwaysSkipItemSkipPolicy());
        faultTolerantStepBuilder.noRollback(TaxWebServiceException.class);

        CompositeItemProcessor<TaxCalculation, PayCheck> compositeItemProcessor = new CompositeItemProcessor<>();
        compositeItemProcessor.setDelegates(Arrays.asList(
                callWebserviceProcessor,
                sendPaycheckProcessor
        ));

        return faultTolerantStepBuilder
                .reader(itemReaderWriterConfig.wsCallItemReader(OVERRIDDEN_BY_EXPRESSION, OVERRIDDEN_BY_EXPRESSION))
                .processor(compositeItemProcessor)
                .writer(itemReaderWriterConfig.wsCallItemWriter())
                .build();
    }

    @Bean
    public Step jobResultsPdf() {
        return stepBuilders.get("JOB_RESULTS_PDF")
                .tasklet(jobResultsTasklet)
                .build();
    }
}
