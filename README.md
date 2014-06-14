Spring Batch Demo
=================

>### Taxcalculation for employees to learn about Spring Batch 


Table of contents
-----------------
* [Introduction](#introduction)
* [Show me the application!](#application)
* [Show me the code!](#code)
* [Team members](#team-members)

<a name="introduction"></a>Introduction
---------------------------------------
To learn more about Spring Batch at Cegeka, we created a sample project where a company must calculate and pay taxes to the government for each of it's employees.

### High level requirements
For each employee, the following needs to be done:
* first the taxes that need to be paid are calculated
* then, a webservice of the government is called to pay these taxes
* last but not least, a PDF paycheck is generated.

A web-based gui is needed so that:
* the list of employees can be consulted
* the taxcalculation jobs of the last 6 months can be consulted
** if a job failed for a certain month, that month should be marked as an error
** if a job failed for a certain month, one should be able to restart the job for that month
** if a job succeeded for a month, one should not be able to restart the job for that month
* for each job that ran, a job result pdf must be generated that shows how much taxes have been paid to the government and how much taxes could not be paid 

###  Non functional requirements
* If the taxcalculation fails for some reason, the job should stop immediately as this code is completely under our control
* If a webservice call fails because of a server error (Http status 5xx) for an employee, it should retry 3 times before passing on to the next employee.
* If for one employee the job was unable to call the webservice due to a server error (Http status 5xx), the job should continue processing all other employees but the job itself should fail so it can be restarted.
* If the webservice calls continue to fail with server errors (Http status 5xx), for 3 consecutive employees, the job should stop. 
* If a webservice call fails because of a client error (Http status 4xx) for an employee, the job should stop immediately.
* If a job is restarted because of failures in the previous run, the webservice calls that already happened for the other employees should not be done again so that we don't pay taxes twice for that employee.


<a name="application"></a>Show me the application!
--------------------------------------------------
To see the end result without setting up Tomcats, Java, ... we created a Vagrant box. How to do so?
```sh
git clone https://github.com/cegeka/batchers.git
cd batchers/taxcalculator-vagrant
vagrant up standalone
# take a break :) it takes a lot of time to build the macine (it depends a lot on your Internet speed)
```
Open a web browser and go to [http://localhost:9090/taxcalculator/#/](http://localhost:9090/taxcalculator/#/)

More info and master/slave config with Vagrant [here](https://github.com/cegeka/batchers/tree/master/taxcalculator-vagrant/README.md)


<a name="code"></a>Show me the code!
------------------------------------
All info about the code and how to install the project and run it, can be found [here](https://github.com/cegeka/batchers/tree/master/taxcalculator/README.md) 


<a name="team-members"></a>Team members
---------------------------------------
* Alex Polatos <Alex.Polatos@cegeka.com>
* Andrei Petcu <Andrei.Petcu@cegeka.com>
* Cristina Muntean <Cristina.Muntean@cegeka.com>
* Edward Moraru
* Madalin Balan <Madalin.Balan@cegeka.com>
* Monica Turiac <Monica.Turiac@cegeka.com>
* Radu Cirstoiu
* Ronald Dehuysser <Ronald.Dehuysser@cegeka.com>
* Silviu Catarau <Silviu.Catarau@cegeka.com>
* Tom Briers <Tom.Briers@cegeka.com>