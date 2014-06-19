#!/usr/bin/env bash
set -e

export DISPLAY=:10

tar -xf apache-tomcat-7.0.54.tar.gz

rm -rf apache-tomcat-7.0.54-presentation
mkdir apache-tomcat-7.0.54-presentation
tar --strip-components 1 -C apache-tomcat-7.0.54-presentation -xzf apache-tomcat-7.0.54.tar.gz

#change tomcat port form 8080 to 9090
sed -i 's/"8080"/"9090"/g' apache-tomcat-7.0.54-presentation/conf/server.xml

rm -rf apache-tomcat-7.0.54/webapps/taxcalculator-*
cp batchers/taxcalculator/taxcalculator-presentation/target/taxcalculator-presentation-1.0-SNAPSHOT.war apache-tomcat-7.0.54-presentation/webapps/taxcalculator.war

# source it only if exits
if [ -f /etc/profile.d/batchers.sh ]; then source /etc/profile.d/batchers.sh ;fi

echo "$USER BATCHERS_PROFILE=$BATCHERS_PROFILE"
MY_JAVA_OPTS="export JAVA_OPTS=\"-DAPP_ENV=staging\""
if [ "$BATCHERS_PROFILE" = "master" ]; then
    MY_JAVA_OPTS="export JAVA_OPTS=\"-Dspring.profiles.active=remotePartitioningMaster -DAPP_ENV=staging\""
fi
if [ "$BATCHERS_PROFILE" = "slave" ]; then
    MY_JAVA_OPTS="export JAVA_OPTS=\"-Dspring.profiles.active=remotePartitioningSlave -DAPP_ENV=staging\""
fi
echo "$USER MY_JAVA_OPTS=$MY_JAVA_OPTS"

echo $MY_JAVA_OPTS | tee apache-tomcat-7.0.54-presentation/bin/setenv.sh
chmod +x apache-tomcat-7.0.54-presentation/bin/setenv.sh

apache-tomcat-7.0.54-presentation/bin/catalina.sh start
