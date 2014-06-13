package be.cegeka.batchers.taxcalculator.batch.config.singlejvm;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.service.TaxWebServiceNonFatalException;
import be.cegeka.batchers.taxcalculator.batch.config.AbstractEmployeeJobConfig;
import be.cegeka.batchers.taxcalculator.batch.config.TempConfigToInitDB;
import be.cegeka.batchers.taxcalculator.batch.config.listeners.*;
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
public class EmployeeJobConfigSingleJvm extends AbstractEmployeeJobConfig {

    public static final String EMPLOYEE_JOB = "employeeJob";
    public static final String TAX_CALCULATION_STEP = "taxCalculationStep";
    private static final String WS_CALL_AND_GENERATE_AND_SEND_PAYCHECK_STEP = "wsCallAndGenerateAndSendPaycheckStep";
    public static final String JOB_RESULTS_PDF_STEP = "jobResultsPdfStep";

    @Autowired
    private JobBuilderFactory jobBuilders;
    @Autowired
    private JobStatusListener jobStatusListener;
    @Autowired
    private ChangeStatusOnFailedStepsJobExecListener changeStatusOnFailedStepsJobExecListener;


    @Bean
    public Job employeeJob() {
        return jobBuilders.get(EMPLOYEE_JOB)
                .start(taxCalculationStep())
                .next(wsCallAndGenerateAndSendPaycheckStep())
                .next(jobResultsPdf())
                .listener(jobStatusListener)
                .listener(changeStatusOnFailedStepsJobExecListener)
                .build();
    }


    @Bean
    public Step taxCalculationStep() {
        return taxCalculationStep(TAX_CALCULATION_STEP);
    }

    @Bean
    public Step wsCallAndGenerateAndSendPaycheckStep() {
        return wsCallAndGenerateAndSendPaycheckStep(WS_CALL_AND_GENERATE_AND_SEND_PAYCHECK_STEP);
    }

    @Bean
    protected Step jobResultsPdf() {
        return jobResultsPdf(JOB_RESULTS_PDF_STEP);
    }
}
