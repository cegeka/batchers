package be.cegeka.batchers.taxcalculator.batch.config.remotepartitioning;

import be.cegeka.batchers.taxcalculator.application.domain.PayCheck;
import be.cegeka.batchers.taxcalculator.application.domain.TaxCalculation;
import be.cegeka.batchers.taxcalculator.application.service.TaxWebServiceException;
import be.cegeka.batchers.taxcalculator.batch.config.ItemReaderWriterConfig;
import be.cegeka.batchers.taxcalculator.batch.config.TempConfigToInitDB;
import be.cegeka.batchers.taxcalculator.batch.config.listeners.ChangeStatusOnFailedStepsJobExecListener;
import be.cegeka.batchers.taxcalculator.batch.config.listeners.FailedStepStepExecutionListener;
import be.cegeka.batchers.taxcalculator.batch.config.skippolicy.MaxConsecutiveNonFatalTaxWebServiceExceptionsSkipPolicy;
import be.cegeka.batchers.taxcalculator.batch.processor.CallWebserviceProcessor;
import be.cegeka.batchers.taxcalculator.batch.processor.SendPaycheckProcessor;
import be.cegeka.batchers.taxcalculator.batch.tasklet.JobResultsTasklet;
import be.cegeka.batchers.taxcalculator.infrastructure.config.PropertyPlaceHolderConfig;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.integration.partition.MessageChannelPartitionHandler;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.amqp.outbound.AmqpOutboundEndpoint;
import org.springframework.integration.annotation.Aggregator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessagingTemplate;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
@EnableBatchProcessing
@ComponentScan(basePackages = "be.cegeka.batchers.taxcalculator.batch")
@Import({PropertyPlaceHolderConfig.class, TempConfigToInitDB.class, ItemReaderWriterConfig.class})
@PropertySource("classpath:taxcalculator-batch.properties")
@Profile(value = {"remotePartitioning", "testRemotePartitioning"})
public class EmployeeJobConfigMaster extends DefaultBatchConfigurer {

    public static final String EMPLOYEE_JOB = "employeeJobRemotePartitioning";
    public static final String TAX_CALCULATION_STEP = "taxCalculationMasterStep";
    private static final String WS_CALL_STEP = "wsCallStep";

    public static final String ROUTING_KEY_REQUESTS = "routingKeyRequests";
    public static final String ROUTING_KEY_REPLIES = "routingKeyReplies";

    private static Long OVERRIDDEN_BY_EXPRESSION = null;
    private static StepExecution OVERRIDDEN_BY_EXPRESSION_STEP_EXECUTION = null;

    @Autowired
    private JobBuilderFactory jobBuilders;
    @Autowired
    private StepBuilderFactory stepBuilders;
    @Autowired
    private ItemReaderWriterConfig itemReaderWriterConfig;
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
    private TaskExecutor taskExecutor;

    @Autowired
    private EmployeeJobPartitioner employeeJobPartitioner;

    @Value("${rabbitmq.ip}")
    private String rabbitmqAddress;
    @Value("${rabbitmq.username}")
    private String rabbitmqUsername;
    @Value("${rabbitmq.password}")
    private String rabbitmqPassword;


    @Bean
    public Job employeeJob() {
        return jobBuilders.get(EMPLOYEE_JOB)
                .start(taxCalculationStep())
                .next(wsCallStep())
                .next(jobResultsPdf())
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
    public Step wsCallStep() {
        CompositeItemProcessor<TaxCalculation, PayCheck> compositeItemProcessor = new CompositeItemProcessor<>();
        compositeItemProcessor.setDelegates(Arrays.asList(
                callWebserviceProcessor,
                sendPaycheckProcessor
        ));

        return stepBuilders.get(WS_CALL_STEP)
                .<TaxCalculation, PayCheck>chunk(5)
                .faultTolerant()
                .skipPolicy(maxConsecutiveNonFatalTaxWebServiceExceptionsSkipPolicy)
                .noRollback(TaxWebServiceException.class)
                .reader(itemReaderWriterConfig.wsCallItemReader(OVERRIDDEN_BY_EXPRESSION, OVERRIDDEN_BY_EXPRESSION, OVERRIDDEN_BY_EXPRESSION_STEP_EXECUTION))
                .processor(compositeItemProcessor)
                .writer(itemReaderWriterConfig.wsCallItemWriter())
                .listener(maxConsecutiveNonFatalTaxWebServiceExceptionsSkipPolicy)
                .listener(failedStepStepExecutionListener)
                .listener(sendPaycheckProcessor)
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

    @Bean
    @Aggregator(sendPartialResultsOnExpiry = true, sendTimeout = 60000000, inputChannel = "inboundStaging")
    public PartitionHandler taxCalculationPartitionHandler() {
        MessageChannelPartitionHandler messageChannelPartitionHandler = new MessageChannelPartitionHandler();
        messageChannelPartitionHandler.setGridSize(1);
        messageChannelPartitionHandler.setStepName(TAX_CALCULATION_STEP);

        MessagingTemplate messagingGateway = new MessagingTemplate();
        messagingGateway.setReceiveTimeout(60000000L);
        messagingGateway.setDefaultChannel(new DirectChannel());
        messageChannelPartitionHandler.setMessagingOperations(messagingGateway);

        return messageChannelPartitionHandler;
    }

    @Bean
    public AmqpOutboundEndpoint outboundEndpoint() {
        return new AmqpOutboundEndpoint(amqpTemplate());
    }

    @Bean
    private AmqpTemplate amqpTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setRoutingKey(ROUTING_KEY_REQUESTS);
        rabbitTemplate.setConnectionFactory(connectionFactory());
        rabbitTemplate.setReplyTimeout(60000000L);
        rabbitTemplate.setReplyQueue(replyQueue());

        return rabbitTemplate;
    }

    @Bean
    public SimpleMessageListenerContainer replyListenerContainer() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.setQueues(replyQueue());
        container.setMessageListener(amqpTemplate());

        return container;
    }

    @Bean
    private Queue replyQueue() {
        return new Queue(ROUTING_KEY_REPLIES);
    }

    @Bean
    private Queue requestQueue() {
        return new Queue(ROUTING_KEY_REQUESTS);
    }

    @Bean
    private ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(rabbitmqAddress);
        connectionFactory.setUsername(rabbitmqUsername);
        connectionFactory.setPassword(rabbitmqPassword);
        return connectionFactory;
    }

    @Bean
    private DirectChannel inboundStaging() {
        return new DirectChannel();
    }

    @Bean
    private RabbitAdmin rabbitAdmin() {
        return new RabbitAdmin(connectionFactory());
    }
}
