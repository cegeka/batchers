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
* create one Run/Debug configuration for presentation-war exploded (different port, preferably 9090). Context path: /taxcalculator
* start both servers and connect to [http://localhost:9090/taxcalculator/](http://localhost:9090/taxcalculator/)

# Spring Batch Configuration


# Deployment configuration

There are two system properties that need to be set:
* APP_ENV - either "default" (this is the default setting, using in-memory HSQLDB) or "staging" (using MySQL)
* log_dir - having "target/logs" as default

You can set these at tomcat startup: -DAPP\_ENV=... -Dlog\_dir=...

# Code gotchas

