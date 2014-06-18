Taxcalculator Application
=========================

Table of contents
-----------------
* [Structure of the application](#structure)
* [Installation of the application](#installation)
* [Running the application](#running)

<a name="structure"></a>Structure of the application
----------------------------------------------------
#### 1. Application
Domain and business logic, services for sending emails and generating PDFs

#### 2. Batch
SpringBatch configuration (Jobs/Steps/Reader/Writers/Processors/Listeners definitions). More info [here](https://github.com/cegeka/batchers/tree/master/taxcalculator/taxcalculator-batch/README.md)

#### 3. Batch Api
Api of all services that are needed in the presentation layer so that we can easily switch to another Batch implementation if needed.

#### 4. Infrastructure
InfrastructureConfig, PersistenceConfig and PropertyPlaceHolderConfig

#### 5. Presentation
Shows the employee table and allows the job to be run.
Contains the AngularJs files and rest controllers (for retrieving employees/starting job).

#### 6. Stubwebservice
Simulates the TaxPayment external service.

<a name="installation"></a>Installation of the application
----------------------------------------------------------

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


<a name="running"></a>Running the application
---------------------------------------------
### Configuration
1. Define JNDI resources in tomcat/conf/context.xml under <Context> root like this:
	<Environment name="smtp_server" value="your_smtp_server" type="java.lang.String" />
	<Environment name="smtp_port" value="your_port" type="java.lang.String" />
	<Environment name="smtp_username" value="your_username" type="java.lang.String" />
	<Environment name="smtp_password" value="your_password" type="java.lang.String" />
	
### Run configuration
1. Create one Run/Debug configuration for stubwebservice-war exploded on port 9091, context path: /stubwebservice. Or, use cd taxcalculator-stubwebservice && mvn jetty:run
2. Create one Run/Debug configuration for presentation-war exploded (different port, preferably 9090), context path: /taxcalculator.  Or, (does not pre-populate database with employees) : cd taxcalculator-presentation && mvn tomcat7:run
3. Start both servers and connect to [http://localhost:9090/taxcalculator/](http://localhost:9090/taxcalculator/)

### Deployment configuration

There are two system properties that need to be set:
> __APP_ENV__ - either "default" (this is the default setting, using in-memory HSQLDB) or "staging" (using MySQL) (for __staging__ you need to add in "/etc/hosts" on GNU/Linux operating systems or in "C:\Windows\System32\drivers\etc\hosts" a entry named __batchersmaster__ that points to the IP of the MySQL database)

> __log_dir__ - having "target/logs" as default

You can set these at tomcat startup: -DAPP\_ENV=... -Dlog\_dir=...

It can be configured to timeout and/or fail for specific employees: __taxcalculator-stubwebservice.properties__

__stubwebservice.blacklistemployees__ - employee ids for which the server responds with a 500 internal server error, and how many times

__stubwebservice.timeoutemployees__ - employee ids for which the server times out

