##Why use Spring Batch?

The problem: calculating long running operations for multiple rows in a table, like tax calculations for 300.000 employees. Simple right? Create one big select, pick each employee calculate the tax and update the record.

Well, it's not quite simple. Some operations might fail for some users and we want to restart the operation only for the failed rows. Some operations might depend on external web services which are not idempotent so it is not simple to restart the process only for the employees with failed operations.

Luckily we have [Spring Batch 3.0](http://projects.spring.io/spring-batch/) which takes care of separating our work in chunks, restarting operations that failed, distributing the work across several machines and a lot of functionality surrounding batch operations. All we need to do is provide our domain functionality.

##Spring Batch components
The main component of Spring Batch is the __Job__. The job is a concrete task we want to perform. Each Job as a series of __JobSteps__. The step is a independent process of the Job. And each job may run several times and for that we have __JobExecutions__.

In our example the job is a process that calculates the taxes of a list of employees. This job is made of tree steps: tax calculation, web service call that actually does the tax payment and generating a tax  payment PDF file. The job executions in our case is the time we run the same job in _each month_. Job execution is a job run with some parameters. In our case the parameters are the year and month for which we do the calculation.

This might seem complicated but the actual configuration is done in pure Java code and it's simple to read and understand. No more verbose XML!
```java
    @Bean
    public Job employeeJob() {
        return jobBuilders.get(EMPLOYEE_JOB)
                .start(taxCalculationStep())
                .next(wsCallAndGenerateAndSendPaycheckStep())
                .next(jobResultsPdf())
                .build();
    }
```

Let's dig a little deeper: what is a step? How do we configure it. Until now, this is just configuration and the application does not know anything about our domain. The next Spring Batch objects __ItemReader__, __ItemWriter__ and __ItemProcessor__ actually work with our domain objects. The item reader reads item by item passes it to the item processor and then the processed result is passed to the item writer. By decoupling the reading, processing and writing, Spring Batch makes it easy to have item writes grouped in chunks instead of writing item by item. 

Here is some more configuration code
```java
    protected Step taxCalculationStep() {
        return stepBuilders.get(TAX_CALCULATION_STEP)
                .<Employee, TaxCalculation>chunk(5)
                .reader(taxCalculatorItemReader)
                .processor(calculateTaxProcessor)
                .writer(taxCalculatorItemWriter)
                .build();
    }
```

By configuring the chunk size (in our case is 5) we can pick how many processed items get written in one chunk write. If one chunk fails, the entire transaction is rolled back and none of the items in the chunk get to be written. In this case we can retry or restart the job in order to process the chunk again.

There is still something I did not explain in the code above: what is ```<Employee, TaxCalculation>chunk(5)```? The item reader __reads__ objects of type __Employee__, the processor __processes__ objects of type __Employee into__ objects of type __TaxCalculation__ and the writer __writes__ objects of type __TaxCalculation__). This looks a lot like [UNIX pipes](http://en.wikipedia.org/wiki/Pipeline_%28Unix%29). If the reader reads ```Employee``` and the writer writes ```TaxCalculation``` then the step will have ```Employee``` as input and ```TaxCalculation``` as output. Configuration of reader, processor and writer will make this a lot clearer:
```java
    @Autowired
    private JpaPagingItemReader<Employee> taxCalculatorItemReader;
    @Autowired
    private ItemProcessor<Employee, TaxCalculation> calculateTaxProcessor;
    @Autowired
    private JpaItemWriter<TaxCalculation> taxCalculatorItemWriter;

```

As you can assume from the code above, Spring Batch offers some default implementations of ItemReader and ItemWriter. We are using the JPA version since we use JPA for persistence.

Now the last class we need to check out is the ItemProcessor. The code is also quite self explanatory:
```java

@Component
public class CalculateTaxProcessor implements ItemProcessor<Employee, TaxCalculation> {

    @Autowired
    private TaxCalculatorService taxCalculatorService;

    @Override
    public TaxCalculation process(Employee employee) {
        TaxCalculation taxCalculation = taxCalculatorService.calculateTax(employee);
        return taxCalculation;
    }
}
```

With this I finis a short intro in how spring batch works and gets configured. This is just a simplified version of our code. I kept out some parts for clarity. Please feel free to check out our application since it is [open source](https://github.com/cegeka/batchers). Please check out our future blog post about job executions, retrying and exception handling in Spring Batch.