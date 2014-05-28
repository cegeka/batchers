## Setup 

Install the following:
> Java 8, Maven 3, Tomcat 7

> NodeJS v0.10+ (tested on v0.10.28)

Install Karma, Jasmine and browser launchers by running the following commands:
> npm install -g karma-ng-scenario karma-junit-reporter

> npm install karma-chrome-launcher --save-dev

> npm install karma-firefox-launcher --save-dev

> npm install -g karma-jasmine

> npm install karma-jasmine --save-dev

Set CHROME\_BIN and FIREFOX\_BIN as environment variables, pointing to the executables themselves.

Import the maven projects in IntelliJ/Eclipse and run:
> mvn clean install

## Running the app

1. Create one Run/Debug configuration for stubwebservice-war exploded on port 9091, context path: /stubwebservice. Or, use cd taxcalculator-stubwebservice && mvn jetty:run
2. Create one Run/Debug configuration for presentation-war exploded (different port, preferably 9090), context path: /taxcalculator.  Or, (does not pre-populate database with employees) : cd taxcalculator-presentation && mvn tomcat7:run
3. Start both servers and connect to [http://localhost:9090/taxcalculator/](http://localhost:9090/taxcalculator/)

## Deployment configuration

There are two system properties that need to be set:
> __APP_ENV__ - either "default" (this is the default setting, using in-memory HSQLDB) or "staging" (using MySQL)

> __log_dir__ - having "target/logs" as default

You can set these at tomcat startup: -DAPP\_ENV=... -Dlog\_dir=...

## Project structure

#### 1. Presentation
Shows the employee table and allows the job to be run manually.
Contains the AngularJs files and rest controllers (for retrieving employees/starting job).

#### 2. Application
Domain and business logic, services for sending email and generate PDFs

#### 3. Batch
SpringBatch configuration (Jobs/Steps/Reader/Writers/Processors/Listeners definitions)

#### 4. Infrastructure
PersistenceConfig and PropertyPlaceHolderConfig

#### 5. Stubwebservice
Simulates an external service (eg: payments).

It can be configured to timeout and/or fail for specific employees: __taxcalculator-stubwebservice.properties__

__stubwebservice.blacklistemployees__ - employee ids for which the server responds with a 500 internal server error, and how many times

__stubwebservice.timeoutemployees__ - employee ids for which the server times out

## Spring Batch Configuration
We started from the idea that we will have a list of employees for which we run a __job__ with the following __steps__:
* Step1: A regular step that calculate taxes (read an Employee, calculates the Tax, writes a TaxCalculation)
* Step2: A step with a __composite item processor__ with 3 processors:
    call a webservice to send the tax
    generate a PDF
    email it to the employee
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

## How Tos

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

## 4. Lessons learned (so you don't have to!)

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


## Relevant links

- Spring Batch Reference Documentation: http://docs.spring.io/spring-batch/reference/html/
- Spring Batch presentation at Java Users Group by Michael Minella: https://www.youtube.com/watch?v=CYTj5YT7CZU
- Spring Batch with Java Config: https://blog.codecentric.de/en/2013/06/spring-batch-2-2-javaconfig-part-1-a-comparison-to-xml/
