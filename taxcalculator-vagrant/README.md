Batchers Vagrant
===============
Install
===============

check that you have ports 9090, 9091, 3306, 5672 and 15672 free on your machine

you can do this with "netstat -aon" and search for the specific ports

besides this you need Virtualbox, Vagrant and git installed


```sh
https://github.com/cegeka/batchers.git
cd batchers/taxcalculator-vagrant
vagrant up
# take a break :)
# it takes 28 minutes on my work machine and 17 on my home computer (the main reason is the Internet speed)
```
open in web browser http://localhost:9090/taxcalculator/#/


Usage
===============

If you want to run in master/slave on the same machine run
```sh
vagrant up master slave
```

If you want to run in slave mode but use some other master (let's say it's IP is 121.122.123.124) run
```sh
BATCHERS_MASTER_IP=121.122.123.124 vagrant up slave
```

Master and Standalone use the same ports so if you start them together they will pick different ports.

If you want to destroy and rebuild a image run one or all of the commands:
```sh
vagrant destroy -f master
vagrant destroy -f slave
vagrant destroy -f standalone
```

If you want to ssh to one of the machines run:
```sh
vagrant ssh master (or slave or standalone)
```