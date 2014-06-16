package be.cegeka.batchers.taxcalculator.batch.config.singlejvm;

import be.cegeka.batchers.taxcalculator.batch.config.AbstractEmployeeJobConfig;
import be.cegeka.batchers.taxcalculator.batch.config.TempConfigToInitDB;
import be.cegeka.batchers.taxcalculator.batch.config.listeners.*;
import be.cegeka.batchers.taxcalculator.infrastructure.config.PropertyPlaceHolderConfig;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;

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
