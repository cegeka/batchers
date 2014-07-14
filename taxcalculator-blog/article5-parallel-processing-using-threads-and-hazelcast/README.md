# Parallel and distributed processing with Spring Batch and HazelCast

In our TaxCalculator application we now know how long a job takes. And it's time to speed things up. In our application we take two approaches to increase our performance with Spring Batch, parallel processing and remote partitioning using HazelCast:

## 1. Parallel chunks processing
Thanks to parallel processing, several chunks can be executed in parallel on the server. In the definition of a step we provide a __TaskExecutor__. In our tests, we off course provide a __SyncTaskExecutor__ but that doesn't help in production for increasing the performance. Spring provides the __SimpleAsyncTaskExecutor__ out-of-the-box which does alle the heavy lifting for us.

Here we create a _TaskExecutor_ that uses multiple threads:

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

And in the definition of a step we use that _TaskExecutor_:

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

## 2. Remote partitioning with HazelCast
But, things aren't fast enough just yet. And thanks to remote partitioning, we can harvest the processing power of a cluster of machines. Spring supports this out-of-the-box thanks to AMQP and RabbitMQ... but it's horrible to setup and there are not a lot of examples out there yet on how to do it in pure JavaConfig. After having lost almost lost three days, we gave up on the integration of Spring Batch with RabbitMQ for remote partitioning.

But, no worries, HazelCast to the rescue! HazelCast is an in-memory data grid with some other features like a distributed queue. Integrating HazelCast with Spring Batch went like a breeze and we have the added benefit that we don't have to setup RabbitMQ. 

So, how do we integrate HazelCast with Spring Batch? We will use the Master-Slave Pattern where the master will distribute the work and the slaves will process the work provided by the master.

But first, we create a special Spring Configuration which defines everything related to HazelCast.

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
	}
```

The master has the responsibility of creating and sending partitions over a communication channel to a queue where the slaves are listening. Since the communication channel can be a __BlockingQueue__, we can use the distributed queue from HazelCast. In this config, we define two queue's: the _requests_ queue on which the Master will distribute the work and the _results_ queue on which slaves signal that an item is processed.


To enable the Master-Slave setup, we are using Spring Profiles: __remotePartitioningMaster__ and __remotePartitioningSlave__
For launching a master configuration we pass as jvm parameter: ```-Dspring.profiles.active=remotePartitioningMaster```. Similar for the slaves.

### Master configuration
In our master configuration, [EmployeeJobConfigMaster](https://github.com/cegeka/batchers/blob/master/taxcalculator/taxcalculator-batch/src/main/java/be/cegeka/batchers/taxcalculator/batch/config/remotepartitioning/EmployeeJobConfigMaster.java), we define a step that does the partitioning using a partitioner and a partitionHandler defined below:

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

	@Bean
    public QueueChannel replyChannel() {
        return new QueueChannel();
    }
```

The master automatically takes into account how many slaves there are thanks to the _clusterConfig.getClusterSize()_ method so it can optimize the workload. Do not forget to annotate the _EmployeeJobConfigMaster_ class with __@EnableIntegration__ annotation so that Spring knows it must process the annotations like @__Aggregator__.


### Slave configuration
Our slave configuration, [EmployeeJobConfigSlave](https://github.com/cegeka/batchers/blob/master/taxcalculator/taxcalculator-batch/src/main/java/be/cegeka/batchers/taxcalculator/batch/config/remotepartitioning/EmployeeJobConfigSlave.java) is also annotated with the annotation __@EnableIntegration__ and allows us to use the __@ServiceActivator__. The method annotated with the __@ServiceActivator__, takes in the work from the _inboudRequests_ queue provided by the master, processes it and then signals on the _outboundResults_ queue that the work is done.

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
And that's it! Integrating Spring Batch with HazelCast is easy-peasy and results in less code and in easier configuration and installation. We recommend this setup instead of using the Spring preferred way with RabbitMQ. 