## Setup

Install the following:
* Java 8, maven 3, tomcat 7
* NodeJS v0.10+ (tested on v0.10.28)

 
Install Karma, Jasmine and browser launchers by running the following commands:
* npm install -g karma-ng-scenario karma-junit-reporter
* npm install karma-chrome-launcher --save-dev
* npm install karma-firefox-launcher --save-dev
* npm install -g karma-jasmine
* npm install karma-jasmine --save-dev

Set CHROME\_BIN and FIREFOX\_BIN as environment variables, pointing to the executables themselves.

Import the maven projects in IntelliJ/Eclipse and run:
* mvn clean install

## Running the app

* create one Run/Debug configuration for stubwebservice-war exploded on port 9091. Context path: /stubwebservice
* alternative : cd taxcalculator-stubwebservice && mvn jetty:run
* create one Run/Debug configuration for presentation-war exploded (different port, preferably 9090). Context path: /taxcalculator
* alternative (does not pre-populate database with employees) : cd taxcalculator-presentation && mvn tomcat7:run
* start both servers and connect to [http://localhost:9090/taxcalculator/](http://localhost:9090/taxcalculator/)

## Deployment configuration

There are two system properties that need to be set:
* APP_ENV - either "default" (this is the default setting, using in-memory HSQLDB) or "staging" (using MySQL)
* log_dir - having "target/logs" as default

You can set these at tomcat startup: -DAPP\_ENV=... -Dlog\_dir=...

## Project structure

1. application
contains the domain + business logic, services for sending email + generate PDFs

2. batch
SpringBatch configuration (Jobs/Steps/Reader/Writers/Processors/Listeners definitions)

3. infrastructure
PersistenceConfig + PropertyPlaceHolderConfig

4. stubwebservice
Simulates an external service (eg: payments).

It can be configured to timeout&fail for specific employees: taxcalculator-stubwebservice.properties

stubwebservice.blacklistemployees - employee ids for which the server responds with a 500 internal server error, and how many times

stubwebservice.timeoutemployees - employee ids for which the server times out

5. presentation
Shows the employee table and allows the job to be run manually.

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


## HOW TOs

- Attempt to process all items, despite running into exceptions (eg: external services)
Use a unique identifier for each job (eg: current time), and make all other parameter non-identifiable
Use a "always skip policy"
Track execution results in the DB
Write the query *carefully* :)

## Lessons learned (so you don't have to!)

- There should be just one transaction manager, shared between JPA and Spring, therefore our Job config extends __DefaultBatchConfigurer__. This provides a default job repository and job launcher.

- Integration testing (see __AbstractIntegrationTest__)
Spring Batch provites some utility classes for testing, such as JobLauncherTestUtils (allows running jobs or steps) and JobRepositoryTestUtils (allows removing job executions from the JobRepository)

- Idempotent operations make reasoning about retry/failure scenarios a lot easier. When an operation is not idempotent you can create a wrapper for that action that is idempotent

- Using retry templates: see __CallWebserviceProcessor__ for configuring retry within a step

- So, an exception occurs in processing an item...
Default/No Skip Policy - the processing does not continue, the job execution is failed
Skip Policy - if the exception can be skipped, then the current chunk is rolled back and reexecuted without the item w/ exception
No-Rollback - if the exception is configured not to trigger a roll-back, the processing of the current chunk continues

- When using paging item readers, the item reader query MUST NOT change size during the step execution.

## Relevant links