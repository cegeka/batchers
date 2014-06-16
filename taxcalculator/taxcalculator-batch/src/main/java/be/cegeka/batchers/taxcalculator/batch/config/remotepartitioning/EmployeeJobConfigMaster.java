package be.cegeka.batchers.taxcalculator.batch.config.remotepartitioning;

import be.cegeka.batchers.taxcalculator.batch.config.AbstractEmployeeJobConfig;
import be.cegeka.batchers.taxcalculator.batch.config.TempConfigToInitDB;
import be.cegeka.batchers.taxcalculator.batch.config.listeners.ChangeStatusOnFailedStepsJobExecListener;
import be.cegeka.batchers.taxcalculator.batch.config.listeners.JobStatusListener;
import be.cegeka.batchers.taxcalculator.infrastructure.config.PropertyPlaceHolderConfig;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.integration.partition.MessageChannelPartitionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.integration.annotation.Aggregator;
import org.springframework.integration.annotation.Payloads;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessagingTemplate;

import java.util.List;

@Configuration
@EnableBatchProcessing
@EnableIntegration
@ComponentScan(basePackages = "be.cegeka.batchers.taxcalculator.batch")
@Import({PropertyPlaceHolderConfig.class, TempConfigToInitDB.class, ItemReaderWriterConfig.class})
@PropertySource("classpath:taxcalculator-batch.properties")
@Profile(value = {"remotePartitioningMaster", "testRemotePartitioning"})
public class EmployeeJobConfigMaster extends AbstractEmployeeJobConfig {

    private static final int MASTER_WITHOUT_TAX_CALCULATION_STEP = 1;

    public static final String WS_CALL_AND_GENERATE_AND_SEND_PAYCHECK_STEP = "wsCallAndGenerateAndSendPaycheckStep";
    public static final String JOB_RESULTS_PDF_STEP = "jobResultsPdfStep";

    public static final String EMPLOYEE_JOB = "employeeJobRemotePartitioning";
    public static final String TAX_CALCULATION_STEP = "taxCalculationMasterStep";
    public static final long RECEIVE_TIMEOUT = 60000000L;

    @Autowired
    private JobBuilderFactory jobBuilders;
    @Autowired
    private JobStatusListener jobStatusListener;
    @Autowired
    private ChangeStatusOnFailedStepsJobExecListener changeStatusOnFailedStepsJobExecListener;

    @Autowired
    private ClusterConfig clusterConfig;
    @Autowired
    private EmployeeJobPartitioner employeeJobPartitioner;

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
        return stepBuilders
                .get(TAX_CALCULATION_STEP)
                .partitioner(TAX_CALCULATION_STEP, employeeJobPartitioner)
                .partitionHandler(taxCalculationPartitionHandler())
                .build();
    }

    @Bean
    public Step wsCallAndGenerateAndSendPaycheckStep() {
        return wsCallAndGenerateAndSendPaycheckStep(WS_CALL_AND_GENERATE_AND_SEND_PAYCHECK_STEP);
    }

    @Bean
    protected Step jobResultsPdf() {
        return jobResultsPdf(JOB_RESULTS_PDF_STEP);
    }

    @Bean
    @StepScope
    public PartitionHandler taxCalculationPartitionHandler() {
        MessageChannelPartitionHandler messageChannelPartitionHandler = new MessageChannelPartitionHandler();
        messageChannelPartitionHandler.setGridSize(clusterConfig.getClusterSize() - MASTER_WITHOUT_TAX_CALCULATION_STEP);
        messageChannelPartitionHandler.setReplyChannel(replyChannel());
        messageChannelPartitionHandler.setStepName(EmployeeJobConfigSlave.TAX_CALCULATION_STEP);

        MessagingTemplate messagingGateway = new MessagingTemplate();
        messagingGateway.setReceiveTimeout(RECEIVE_TIMEOUT);
        messagingGateway.setDefaultChannel(outboundRequests());
        messageChannelPartitionHandler.setMessagingOperations(messagingGateway);

        return messageChannelPartitionHandler;
    }

    //why comment: needed because non serializable objects are removed from the messages, including the replychannel.
    //since the reply channel is removed, the messageChannelPartitionHandler never receives any message.
    //here we collect all the results which are incoming and send them to the replyChannel so that the partitionHandler can wait for the results
    @Aggregator(sendPartialResultsOnExpiry = true, sendTimeout = RECEIVE_TIMEOUT, inputChannel = "inboundResults", outputChannel = "replyChannel",
            poller = @Poller(maxMessagesPerPoll = "5", fixedDelay = "10000"))
    public List<?> aggregate(@Payloads List<?> messages) {
        return messages;
    }

    @Bean
    public QueueChannel outboundRequests() {
        return new QueueChannel(clusterConfig.requests());
    }

    @Bean
    public QueueChannel inboundResults() {
        return new QueueChannel(clusterConfig.results());
    }

    @Bean
    public QueueChannel replyChannel() {
        return new QueueChannel();
    }

}
