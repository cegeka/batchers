batchers-master
===============
Install
===============

check that you have ports 9090, 9091, 3306, 5672 and 15672 free on your machine

you can do this with "netstat -aon" and search for the specific ports

besides this you need Vagrant and git installed

l
```sh
git clone https://github.com/andreicristianpetcu/batchers-master.git
cd batchers-master
vagrant up
# take a break :)
# it takes 17 minutes to build on my machine
firefox http://localhost:9090/taxcalculator/#/
```