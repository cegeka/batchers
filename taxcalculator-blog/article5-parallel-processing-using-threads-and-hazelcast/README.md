# Parallel processing

In our application we take two approaces to achieve this with Spring Batch:

## 1. Parallel chunks processing

In the definition of a step we provide a __TaskExecutor__ that will carry on executing the processor for each chunk.

This is an example of definition of a TaskExecutor that uses multiple threads:
```java
@Configuration
@Profile("!test")
public class TaskExecutorConfig {

    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }
}
```

And a definition of a step that configures the TaskExecutor:

```java
 @Autowired
    private TaskExecutor taskExecutor;

    protected Step taxCalculationStep(String stepName) {
        return stepBuilders
                .get(stepName)
                .<Employee, TaxCalculation>chunk(5)
                .reader(taxCalculatorItemReader)
                .processor(calculateTaxProcessor)
                .writer(taxCalculatorItemWriter)
                .taskExecutor(taskExecutor)
                .listener(taxCalculationStepProgressListener())
                .allowStartIfComplete(true)
                .build();
    }
```	

## 2. Remote partitioning

Using this approach allows us to make use of processing power of a cluster of machines.

This setup implies defining a master configuration and a number of slaves.

### Master configuration

Has the responsibility of creating and sending partitions over a communication channel to a queue where the slaves are listening.

For doing the communication we are using HazelCast. HazelCast requires no configuration, it works out of the box.
We keep cluster related definitions in this configuration class:
```java
@Configuration
@Profile(value = {"remotePartitioningMaster", "remotePartitioningSlave", "testRemotePartitioning"})
public class ClusterConfig {
    public int getClusterSize() { 
        return hazelcastInstance().getCluster().getMembers().size();
    }
    @Bean
    public BlockingQueue<Message<?>> requests() {
        return hazelcastInstance().getQueue("requests");
    }
    @Bean
    public BlockingQueue<Message<?>> results() {
        return hazelcastInstance().getQueue("results");
    }   
    @Bean
    public HazelcastInstance hazelcastInstance() {
        Config cfg = new Config();
        HazelcastInstance hz = Hazelcast.newHazelcastInstance(cfg);
        return hz;
    }
```

In our master configuration we have defined a step that uses partitioning with a partitioner and a partitionHandler defined below:
```java
    @Bean
    public Step taxCalculationStep() {
        return stepBuilders
                .get(TAX_CALCULATION_STEP)
                .partitioner(TAX_CALCULATION_STEP, employeeJobPartitioner)
                .partitionHandler(taxCalculationPartitionHandler())
                .build();
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
```
### Slave configuration
The important part here is the method that is annotated with __@ServiceActivator__ and a proxy is created and each time a new request is pushed the StepExecutionRequestHandler's __handle__ method is invoked.

```java
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
```