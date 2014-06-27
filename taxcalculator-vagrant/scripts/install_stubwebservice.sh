#!/usr/bin/env bash
set -e

if [ -f /etc/profile.d/batchers.sh ]; then source /etc/profile.d/batchers.sh ;fi

# unarchive tomcat
tar -xf apache-tomcat-7.0.54.tar.gz

rm -rf apache-tomcat-7.0.54-stubwebservice
mkdir apache-tomcat-7.0.54-stubwebservice
tar --strip-components 1 -C apache-tomcat-7.0.54-stubwebservice -xzf apache-tomcat-7.0.54.tar.gz

#webservice REST port
sed -i 's/"8080"/"9091"/g' apache-tomcat-7.0.54-stubwebservice/conf/server.xml

#override tomcat management ports so they don't conflict with presentation:
sed -i 's/"8009"/"8010"/g' apache-tomcat-7.0.54-stubwebservice/conf/server.xml
sed -i 's/"8005"/"8006"/g' apache-tomcat-7.0.54-stubwebservice/conf/server.xml

rm -rf apache-tomcat-7.0.54-stubwebservice/webapps/taxcalculator-*
cp batchers/taxcalculator/taxcalculator-stubwebservice/target/taxcalculator-stubwebservice-1.0-SNAPSHOT.war apache-tomcat-7.0.54-stubwebservice/webapps/stubwebservice.war

apache-tomcat-7.0.54-stubwebservice/bin/catalina.sh start

