# Setup

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

# Running the app

* create one Run/Debug configuration for stubwebservice-war exploded on port 9091. Context path: /stubwebservice
* alternative : cd taxcalculator-stubwebservice && mvn jetty:run
* create one Run/Debug configuration for presentation-war exploded (different port, preferably 9090). Context path: /taxcalculator
* alternative (does not pre-populate database with employees) : cd taxcalculator-presentation && mvn tomcat7:run
* start both servers and connect to [http://localhost:9090/taxcalculator/](http://localhost:9090/taxcalculator/)

# Spring Batch Configuration
We started from the idea that we will have a list of employees for which we:
* calculate taxes
* call a webservice to send the tax
* generate a PDF

At the end of the job we create a new PDF with results from the job (like sum of taxes).

Our main configuration class for the job is __EmployeeJobConfig__.
There should be just one transaction manager, shared between JPA and Spring, therefore our Job config extends __DefaultBatchConfigurer__. This provides a default job repository and job launcher.

The __ProcessorConfig__ class defines defines the processors of the item (employee).

# Deployment configuration

There are two system properties that need to be set:
* APP_ENV - either "default" (this is the default setting, using in-memory HSQLDB) or "staging" (using MySQL)
* log_dir - having "target/logs" as default

You can set these at tomcat startup: -DAPP\_ENV=... -Dlog\_dir=...

Project structure

1. application 
	- domain + business logic + send email + generate PDFs
2. batch 
	- SpringBatch implementation + EmployeeJobConfig
3. infrastructure
	- PersistenceConfig + PropertyPlaceHolderConfig
4. stubwebservice
	- tax service 
	- listen for calls
	- configure for timeout
	- configure for fail
5. presentation
	- one page - employees table
	- run job functions




