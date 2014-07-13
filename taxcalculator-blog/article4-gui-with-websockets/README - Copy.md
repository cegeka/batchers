#Starting batches and showing progress via Guava EventBus and websockets...
So, you now have a fully functional Batch Job that is crunching and calculating the taxes and creating paychecks for the employees. But how to start it? And how to see how much progress is already done?

Off course, most Batch Jobs are scheduled and run automatically at night. But did the job succeed? Did all of the employees receive their paycheck? And how fast did the job run? Well, stay tuned to find out the answer on all of these questions.

###Spring Batch Admin
You might think: "Hey, aren't you reinventing the wheel? There is Spring Batch Admin!" We know, and we checked it out but the project's website hasn't been updated in a while and we wanted to learn a little bit more about Websockets.


##Rest for bootstrapping
Scheduling is not part of Spring Batch - and for a very good reason: there are better tools to handle that (CRON anyone?). So, we decided to expose the boostrapping with a REST api. This way, it can easily be scheduled via CRON and we can even write a nice little webapp to start it when showing the application to our customer.

Creating the REST controller is really easy thanks to Spring Web MVC: 

```java

	@Controller
	public class JobRestController {
	    private static final Logger LOG = LoggerFactory.getLogger(JobRestController.class);
	    @Autowired
	    private JobService jobService;
	
	    @RequestMapping(value = "runJob/{year}/{month}", method = RequestMethod.POST)
	    @ResponseBody
	    public void runJob(@PathVariable("year") Long year, @PathVariable("month") Long month) {
	    	jobService.runTaxCalculatorJob(new JobStartParams(year, month));
	    }
	
	}
```

As you can see, we just pass the necessary parameters: the year and the month. The _JobService_ itself is also rather simple:

```java

	@Service
	public class TaxCalculatorJobService implements JobService {
	    private static final Logger LOG = LoggerFactory.getLogger(TaxCalculatorJobService.class);
	
	    @Autowired
	    private Job employeeJob;
	
	    @Autowired
	    private JobLauncher jobLauncher;
	
	    @Override
	    public void runTaxCalculatorJob(JobStartParams jobStartParams) {
	        startJobs(jobStartParams.getYear(), jobStartParams.getMonth());
	    }
	
	    protected void startJobs(long year, long month) {
	        try {
	            JobParameters jobParameters = new JobParametersBuilder()
	                    .addLong("month", month)
	                    .addLong("year", year)
	                    .toJobParameters();
	
	            LOG.info("Running job in jobservice");
	            jobLauncher.run(employeeJob, jobParameters);
	        } catch (JobExecutionAlreadyRunningException | JobRestartException
	                | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
	            LOG.error("Job running failed", e);
	        }
	    }
	}
```

Thanks to Spring's _autowiring_ capabilities and the _JobLauncher_ we can easily start the batch with the required parameters.

##Displaying progress
So, your client wants to see how long a job takes. Well, thanks to the combination of _JobExecutionListeners_, _StepExecutionListeners_, _Guava EventBus_, _Spring Messaging_ and _SockJS_  this can be done easily! Historically sending messages from the server to the browser has been hard for developers and servers as well. Usually we had to resort to some sort of periodic polling and stressing the server with requests that do not carry any data. But, today we have WebSockets and best of all, Spring integration for WebSockets!

How to achieve this then? Well, it involves 3 steps:
###1. Publish progress from the Job to Guava EventBus

```java

	public class SingleJVMJobProgressListener implements JobProgressListener {
	
	    private AtomicInteger lastPercentageComplete;
	    private AtomicLong currentItemCount;
	    private int totalItemCount;
	    private JobStartParams jobStartParams;
	    private String stepName;
	
	    @Autowired
	    private EmployeeService employeeService;
	
	    @Autowired
	    private EventBus eventBus;
	
	    @Override
	    public void beforeStep(StepExecution stepExecution) {
	        totalItemCount = employeeService.getEmployeeCount().intValue();
	        jobStartParams = new JobStartParamsMapper().map(stepExecution.getJobParameters());
	        stepName = stepExecution.getStepName();
	        currentItemCount = new AtomicLong();
	        lastPercentageComplete = new AtomicInteger();
	        eventBus.post(new JobProgressEvent(jobStartParams, stepName, 0));
	    }
	
		@Override
	    public void afterWrite(List items) {
	        currentItemCount.addAndGet(items.size());
	        int percentageComplete = currentItemCount.intValue() * 100 / totalItemCount;
	
	        sendUpdateIfNeeded(percentageComplete);
	    }
	
	    private synchronized void sendUpdateIfNeeded(int percentageComplete) {
	        if (percentageComplete > lastPercentageComplete.get()) {
	            lastPercentageComplete = new AtomicInteger(percentageComplete);
	            eventBus.post(new JobProgressEvent(jobStartParams, stepName, lastPercentageComplete.intValue()));
	        }
	    }
	}
```

Before the step is actually started, we check how much items need to be processed. And then after each chunk is written to the database, calculate the amount of items we already processed (in percent) and if that amount is larger than the previous one we already sent, we post a new __JobProgressEvent__ on the _Guava EventBus_. Since we're working in a multithreaded setup, it's important to make sure that you use _AtomicInteger_ and _AtomicLong_ as they are thread safe. 

And thanks to __Guava EventBus__ everything is loosely coupled, always a big advantage.

###2. Sending events to the clients
Thanks to Spring messaging we can easily send messages to the clients that are registered in our [WebSocketController](https://github.com/cegeka/batchers/blob/master/taxcalculator/taxcalculator-presentation/src/main/java/be/cegeka/batchers/taxcalculator/presentation/websockets/WebSocketController.java#L25-L28):

```java

	@Controller
	public class WebSocketController {
	
	    @Autowired
	    private JobService jobService;
	
	    @Autowired
	    private MessageSendingOperations<String> messagingTemplate;
	
	    @Subscribe
	    public void onJobEvent(JobEvent jobEvent) {
	        this.messagingTemplate.convertAndSend("/jobinfo-updates", jobEvent);
	    }
	}
```

We just subscribe ourself on the EventBus (thanks to the __@Subscribe__ annotation) to all the JobEvents and send them to the clients (all the browsers) via the __Spring MessagingTemplate__.

For the _WebSocketController_ to work, we need some configuration: 

```java

	@Configuration
	@EnableWebSocketMessageBroker
	public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {
	
	    @Override
	    public void configureMessageBroker(MessageBrokerRegistry config) {
	        config.enableSimpleBroker("/jobinfo-updates");
	        config.setApplicationDestinationPrefixes("/app");
	    }
	
	    @Override
	    public void registerStompEndpoints(StompEndpointRegistry registry) {
	        registry.addEndpoint("/jobinfo").withSockJS();
	    }
	
	}
```

Here we create a SockJS endpoint for _/jobinfo_. Any client that registers on that endpoint, will receive our _JobEvents_ thanks to the SimpleBroker we enable.

###3. A webpage that listens for updates!
The [client webpage](https://github.com/cegeka/batchers/blob/master/taxcalculator/taxcalculator-presentation/src/main/webapp/resources/js/jobresult/jobresult-controllers.js#L43-L76) simply creates a SockJS client and listens for messages in an Angular controller.

      var socket = new SockJS('/taxcalculator/rest/jobinfo');
      var client = Stomp.over(socket);

      client.connect({}, function (frame) {
        client.subscribe("/jobinfo-updates", function (message) {
          var message = angular.fromJson(message.body);
          alert('new message ' + message);
          //change ancular $scope with the new message
          //and notify angular of the changes
          $scope.$apply();
        });
      });

And that's it! That's how you send messages from the server to the client over web sockets. This was not only fast to develop but it is also easy to understand. Check out our project on github and see the rest of our blog posts.