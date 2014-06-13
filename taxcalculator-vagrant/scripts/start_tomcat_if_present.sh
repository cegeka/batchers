#!/usr/bin/env bash
set -e

if [ -f /home/vagrant/apache-tomcat-7.0.54-presentation/bin/catalina.sh ]; then 
  sudo -i -H -u vagrant /home/vagrant/apache-tomcat-7.0.54-presentation/bin/catalina.sh start
fi
if [ -f /home/vagrant/apache-tomcat-7.0.54-stubwebservice/bin/catalina.sh ]; then 
  sudo -i -H -u vagrant /home/vagrant/apache-tomcat-7.0.54-stubwebservice/bin/catalina.sh start
fi
