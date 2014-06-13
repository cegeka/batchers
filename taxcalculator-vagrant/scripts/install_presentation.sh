#!/usr/bin/env bash
set -e

export DISPLAY=:10

cd $HOME
tar -xf apache-tomcat-7.0.54.tar.gz

mkdir  apache-tomcat-7.0.54-presentation
tar --strip-components 1 -C apache-tomcat-7.0.54-presentation -xzf apache-tomcat-7.0.54.tar.gz

#change tomcat port form 8080 to 9090
sed -i 's/"8080"/"9090"/g' $HOME/apache-tomcat-7.0.54-presentation/conf/server.xml

rm -rf $HOME/apache-tomcat-7.0.54/webapps/taxcalculator-*
cp $HOME/batchers/taxcalculator/taxcalculator-presentation/target/taxcalculator-presentation-1.0-SNAPSHOT.war $HOME/apache-tomcat-7.0.54-presentation/webapps/taxcalculator.war

echo "export JAVA_OPTS=\"-DAPP_ENV=staging\"" | tee apache-tomcat-7.0.54-presentation/bin/setenv.sh

apache-tomcat-7.0.54-presentation/bin/catalina.sh start
