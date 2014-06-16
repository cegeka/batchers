package be.cegeka.batchers.taxcalculator.batch.config.remotepartitioning;

import be.cegeka.batchers.taxcalculator.batch.config.AbstractEmployeeJobConfig;
import be.cegeka.batchers.taxcalculator.batch.config.TempConfigToInitDB;
import be.cegeka.batchers.taxcalculator.infrastructure.config.PropertyPlaceHolderConfig;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.integration.partition.BeanFactoryStepLocator;
import org.springframework.batch.integration.partition.StepExecutionRequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;

@Configuration
@EnableBatchProcessing
@EnableIntegration
@ComponentScan(basePackages = "be.cegeka.batchers.taxcalculator.batch")
@Import({PropertyPlaceHolderConfig.class, TempConfigToInitDB.class, ItemReaderWriterConfig.class})
@PropertySource("classpath:taxcalculator-batch.properties")
@Profile(value = {"remotePartitioningSlave", "testRemotePartitioning"})
public class EmployeeJobConfigSlave extends AbstractEmployeeJobConfig {

    public static final String TAX_CALCULATION_STEP = "taxCalculationSlaveStep";
    public static final String WS_CALL_AND_GENERATE_AND_SEND_PAYCHECK_STEP = "wsCallAndGenerateAndSendPaycheckSlaveStep";

    @Autowired
    private ClusterConfig clusterConfig;

    @Bean
    @ServiceActivator(inputChannel = "inboundRequests", outputChannel = "outboundResults",
            poller = @Poller(maxMessagesPerPoll = "5", fixedDelay = "10000"))
    public StepExecutionRequestHandler stepExecutionRequestHandler() throws Exception {
        StepExecutionRequestHandler stepExecutionRequestHandler = new StepExecutionRequestHandler();
        stepExecutionRequestHandler.setJobExplorer(jobExplorer());
        stepExecutionRequestHandler.setStepLocator(stepLocator());
        return stepExecutionRequestHandler;
    }

    @Bean
    public BeanFactoryStepLocator stepLocator() {
        return new BeanFactoryStepLocator();
    }

    @Bean(name = TAX_CALCULATION_STEP)
    public Step taxCalculationStep() {
        return taxCalculationStep(TAX_CALCULATION_STEP);
    }

    @Bean
    public QueueChannel inboundRequests() {
        return new QueueChannel(clusterConfig.requests());
    }

    @Bean
    public QueueChannel outboundResults() {
        return new QueueChannel(clusterConfig.results());
    }
}
