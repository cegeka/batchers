Spring Batch Module
===================

Table of contents
-----------------
* [Spring Batch Configuration](#configuration)
* [How to's](#how-to)
* [Lessons learned](#lessons-learned)
* [Relevant links](#links)

<a name="configuration"></a>Spring Batch Configuration
------------------------------------------------------
We started from the idea that we will have a list of employees for which we run a __job__ with the following __steps__:

* Step1: A regular step that calculate taxes (read an Employee, calculates the Tax, writes a TaxCalculation)
* Step2: A step with a __composite item processor__ with 3 processors:
 * call a webservice to send the tax
 * generate a PDF
 * email it to the employee
* Step3: A __tasklet__ that generates a report with the sum of all the taxes calculated in the previous step

Our main configuration class for the job is __EmployeeJobConfig__.

```java
        @Bean
        public Job employeeJob() {
                return jobBuilders.get(EMPLOYEE_JOB)
                        .start(taxCalculationStep())
                        .next(wsCallStep())
                        .next(generatePDFStep())
                        .next(jobResultsPdf())
                        .listener(employeeJobExecutionListener)
                        .build();
        }
```

<a name="how-to"></a>How to's
-----------------------------

#### 1. Attempt to process all items, despite running into exceptions (eg: external services)

- Use a unique identifier for each job (eg: current time), and make all other parameter non-identifiable.
- Use a "always skip policy".
- Track execution results in the DB.
- Write the query *carefully* :).

#### 2. Simplify retry/restart logic
Idempotent operations make retry/failure scenarios a lot easier. When an operation is not idempotent you can create a wrapper for that action that is idempotent

#### 3. Integration testing
> see __AbstractIntegrationTest__

Spring Batch provides utility classes for testing, such as JobLauncherTestUtils (allows running jobs or steps) and JobRepositoryTestUtils (allows removing job executions from the JobRepository)

#### 4. Using retry
> see __CallWebserviceProcessor__ for configuring retry within a step

<a name="lessons-learned"></a>Lessons learned
---------------------------------------------

#### 1. Transaction Management
There should be just one transaction manager, shared between JPA and Spring, therefore our Job config extends __DefaultBatchConfigurer__. This provides a default job repository and job launcher.

#### 2. Exception handling during processing

- __Default/No Skip Policy__ - the processing does not continue, the job execution is failed
- __Skip Policy__ - if the exception can be skipped, then the current chunk is rolled back and reexecuted without the item w/ exception
- __No-Rollback__ - if the exception is configured not to trigger a roll-back, the processing of the current chunk continues

#### 3. Paging paging item readers
The item reader query MUST NOT change size during the step execution.

#### 4. Step Scoped Processor
If a processor is used in a composite, and it should be step scoped, then the processor should also be registered as a listener.


<a name="links"></a>Relevant links
----------------------------------
- Spring Batch Reference Documentation: http://docs.spring.io/spring-batch/reference/html/
- Spring Batch Reference Card on DZone: http://refcardz.dzone.com/refcardz/spring-batch-refcard
- Spring Batch presentation at Java Users Group by Michael Minella: https://www.youtube.com/watch?v=CYTj5YT7CZU
- Spring Batch with Java Config: https://blog.codecentric.de/en/2013/06/spring-batch-2-2-javaconfig-part-1-a-comparison-to-xml/
