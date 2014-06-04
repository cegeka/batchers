package be.cegeka.batchers.taxcalculator.batch.config.singlejvm;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.service.TaxWebServiceNonFatalException;
import be.cegeka.batchers.taxcalculator.batch.config.ItemReaderWriterConfig;
import be.cegeka.batchers.taxcalculator.batch.config.TempConfigToInitDB;
import be.cegeka.batchers.taxcalculator.batch.config.listeners.ChangeStatusOnFailedStepsJobExecListener;
import be.cegeka.batchers.taxcalculator.batch.config.listeners.CreateMonthlyTaxForEmployeeListener;
import be.cegeka.batchers.taxcalculator.batch.config.listeners.FailedStepStepExecutionListener;
import be.cegeka.batchers.taxcalculator.batch.config.skippolicy.MaxConsecutiveNonFatalTaxWebServiceExceptionsSkipPolicy;
import be.cegeka.batchers.taxcalculator.batch.domain.PayCheck;
import be.cegeka.batchers.taxcalculator.batch.domain.TaxCalculation;
import be.cegeka.batchers.taxcalculator.batch.processor.CalculateTaxProcessor;
import be.cegeka.batchers.taxcalculator.batch.processor.CallWebserviceProcessor;
import be.cegeka.batchers.taxcalculator.batch.processor.SendPaycheckProcessor;
import be.cegeka.batchers.taxcalculator.batch.tasklet.JobResultsTasklet;
import be.cegeka.batchers.taxcalculator.infrastructure.config.PropertyPlaceHolderConfig;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.task.TaskExecutor;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
@EnableBatchProcessing
@ComponentScan(basePackages = "be.cegeka.batchers.taxcalculator.batch")
@Import({PropertyPlaceHolderConfig.class, TempConfigToInitDB.class, ItemReaderWriterConfig.class})
@PropertySource("classpath:taxcalculator-batch.properties")
@Profile(value = {"default", "singleJvm", "test"})
public class EmployeeJobConfigSingleJvm extends DefaultBatchConfigurer {

    public static final String EMPLOYEE_JOB = "employeeJob";
    public static final String TAX_CALCULATION_STEP = "taxCalculationStep";
    private static final String WS_CALL_AND_GENERATE_AND_SEND_PAYCHECK_STEP = "wsCallAndGenerateAndSendPaycheckStep";
    private static Long OVERRIDDEN_BY_EXPRESSION = null;
    private static StepExecution OVERRIDDEN_BY_EXPRESSION_STEP_EXECUTION = null;

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
    @Autowired
    private ChangeStatusOnFailedStepsJobExecListener changeStatusOnFailedStepsJobExecListener;
    @Autowired
    private FailedStepStepExecutionListener failedStepStepExecutionListener;
    @Autowired
    private MaxConsecutiveNonFatalTaxWebServiceExceptionsSkipPolicy maxConsecutiveNonFatalTaxWebServiceExceptionsSkipPolicy;
    @Autowired
    private CreateMonthlyTaxForEmployeeListener createMonthlyTaxForEmployeeListener;

    @Autowired
    private TaskExecutor taskExecutor;


    @Bean
    public Job employeeJob() {
        return jobBuilders.get(EMPLOYEE_JOB)
                .start(taxCalculationStep())
                .next(wsCallAndGenerateAndSendPaycheckStep())
                .next(jobResultsPdf())
                .listener(changeStatusOnFailedStepsJobExecListener)
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
                .allowStartIfComplete(true)
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean
    public Step wsCallAndGenerateAndSendPaycheckStep() {
        CompositeItemProcessor<TaxCalculation, PayCheck> compositeItemProcessor = new CompositeItemProcessor<>();
        compositeItemProcessor.setDelegates(Arrays.asList(
                callWebserviceProcessor,
                sendPaycheckProcessor
        ));

        return stepBuilders.get(WS_CALL_AND_GENERATE_AND_SEND_PAYCHECK_STEP)
                .<TaxCalculation, PayCheck>chunk(5)
                .faultTolerant()
                .skipPolicy(maxConsecutiveNonFatalTaxWebServiceExceptionsSkipPolicy)
                .noRollback(TaxWebServiceNonFatalException.class)
                .reader(itemReaderWriterConfig.wsCallItemReader(OVERRIDDEN_BY_EXPRESSION, OVERRIDDEN_BY_EXPRESSION, OVERRIDDEN_BY_EXPRESSION_STEP_EXECUTION))
                .processor(compositeItemProcessor)
                .writer(itemReaderWriterConfig.wsCallItemWriter())
                .listener(createMonthlyTaxForEmployeeListener)
                .listener(maxConsecutiveNonFatalTaxWebServiceExceptionsSkipPolicy)
                .listener(failedStepStepExecutionListener)
                .allowStartIfComplete(true)
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean
    public Step jobResultsPdf() {
        return stepBuilders.get("JOB_RESULTS_PDF")
                .tasklet(jobResultsTasklet)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public JobExplorer jobExplorer(DataSource dataSource) throws Exception {
        JobExplorerFactoryBean factory = new JobExplorerFactoryBean();
        factory.setDataSource(dataSource);
        factory.afterPropertiesSet();
        return factory.getObject();
    }
}
