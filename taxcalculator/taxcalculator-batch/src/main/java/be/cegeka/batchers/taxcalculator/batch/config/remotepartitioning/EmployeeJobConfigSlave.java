package be.cegeka.batchers.taxcalculator.batch.config.remotepartitioning;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.batch.config.ItemReaderWriterConfig;
import be.cegeka.batchers.taxcalculator.batch.config.TempConfigToInitDB;
import be.cegeka.batchers.taxcalculator.batch.domain.TaxCalculation;
import be.cegeka.batchers.taxcalculator.batch.processor.CalculateTaxProcessor;
import be.cegeka.batchers.taxcalculator.infrastructure.config.PropertyPlaceHolderConfig;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.integration.partition.BeanFactoryStepLocator;
import org.springframework.batch.integration.partition.StepExecutionRequestHandler;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.amqp.inbound.AmqpInboundGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@ComponentScan(basePackages = "be.cegeka.batchers.taxcalculator.batch")
@Import({PropertyPlaceHolderConfig.class, TempConfigToInitDB.class, ItemReaderWriterConfig.class})
@PropertySource("classpath:taxcalculator-batch.properties")
@Profile(value = {"remotePartitioning", "testRemotePartitioning"})
public class EmployeeJobConfigSlave extends DefaultBatchConfigurer {

    public static final String TAX_CALCULATION_STEP = "taxCalculationSlaveStep";

    @Autowired
    private JpaPagingItemReader<Employee> taxCalculatorItemReaderSlave;
    @Autowired
    private CalculateTaxProcessor calculateTaxProcessor;

    @Autowired
    private StepBuilderFactory stepBuilders;
    @Autowired
    private ItemReaderWriterConfig itemReaderWriterConfig;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private DataSource datasource;

    @Value("${rabbitmq.ip}")
    private String rabbitmqAddress;
    @Value("${rabbitmq.username}")
    private String rabbitmqUsername;
    @Value("${rabbitmq.password}")
    private String rabbitmqPassword;

    @Bean
    public Step taxCalculationStep() {
        return stepBuilders
                .get(TAX_CALCULATION_STEP)
                .<Employee, TaxCalculation>chunk(5)
                .reader(taxCalculatorItemReaderSlave)
                .processor(calculateTaxProcessor)
                .writer(itemReaderWriterConfig.taxCalculatorItemWriter())
                .allowStartIfComplete(true)
                .taskExecutor(taskExecutor)
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
    private Queue replyQueue() {
        return new Queue(EmployeeJobConfigMaster.ROUTING_KEY_REPLIES);
    }

    @Bean
    private Queue requestQueue() {
        return new Queue(EmployeeJobConfigMaster.ROUTING_KEY_REQUESTS);
    }

    @Bean
    private AmqpInboundGateway amqpInboundGateway() {
        AbstractMessageListenerContainer listener = new SimpleMessageListenerContainer();
        AmqpInboundGateway amqpInboundGateway = new AmqpInboundGateway(listener);
        amqpInboundGateway.setRequestChannel(inboundRequest());
        amqpInboundGateway.setReplyChannel(outboundStaging());
        amqpInboundGateway.setReplyTimeout(60000000L);
        return amqpInboundGateway;
    }

    @Bean
    private MessageChannel outboundStaging() {
        return new DirectChannel();
    }

    @Bean
    private MessageChannel inboundRequest() {
        return new DirectChannel();
    }

    @Bean
    private BeanFactoryStepLocator stepLocator() {
        return new BeanFactoryStepLocator();
    }

    @Bean
    @ServiceActivator(inputChannel = "inboundRequests", outputChannel = "outboundStaging")
    private StepExecutionRequestHandler stepExecutionRequestHandler() throws Exception {
        StepExecutionRequestHandler stepExecutionRequestHandler = new StepExecutionRequestHandler();
        stepExecutionRequestHandler.setJobExplorer(jobExplorer(datasource));
        stepExecutionRequestHandler.setStepLocator(stepLocator());
        return stepExecutionRequestHandler;
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
    private RabbitAdmin rabbitAdmin() {
        return new RabbitAdmin(connectionFactory());
    }
}
