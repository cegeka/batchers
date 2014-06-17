#!/usr/bin/env bash
set -e

if [ -f apache-tomcat-7.0.54-presentation/bin/catalina.sh ]; then 
  apache-tomcat-7.0.54-presentation/bin/catalina.sh start
fi
if [ -f apache-tomcat-7.0.54-stubwebservice/bin/catalina.sh ]; then 
  apache-tomcat-7.0.54-stubwebservice/bin/catalina.sh start
fi
