#!/usr/bin/env bash
set -e

echo "$USER BATCHERS_PROFILE is $BATCHERS_PROFILE"
echo "$USER BATCHERS_MASTER_IP is $BATCHERS_MASTER_IP"

echo "export BATCHERS_PROFILE=$BATCHERS_PROFILE" | tee /etc/profile.d/batchers.sh
echo "export BATCHERS_MASTER_IP=$BATCHERS_MASTER_IP" | tee -a /etc/profile.d/batchers.sh
echo "export DISPLAY=:10" | tee -a /etc/profile.d/batchers.sh

echo "export SMTP_SERVER=smtp.googlemail.com" | tee -a /etc/profile.d/batchers.sh
echo "export SMTP_PORT=465" | tee -a /etc/profile.d/batchers.sh
echo "export SMTP_USERNAME=john.batchers" | tee -a /etc/profile.d/batchers.sh
echo "export SMTP_PASSWORD=taxcalculator" | tee -a /etc/profile.d/batchers.sh

chmod +x /etc/profile.d/batchers.sh

source /etc/profile.d/batchers.sh

export DEBIAN_FRONTEND=noninteractive
apt-get update && apt-get upgrade -y
apt-get install git software-properties-common -y

./scripts/set_master_ip.sh

export PATH="/home/vagrant/.local/bin/karma:$PATH"

./scripts/install_xvfb_and_firefox.sh

./scripts/install_oracle_java8.sh

./scripts/install_mysql.sh

#run script as Vagrant user not as root
sudo -i -H -u vagrant ./scripts/install_as_vagrant.sh

sed -i '/start_tomcat_if_present/d' /etc/crontab
echo "@reboot vagrant /home/vagrant/scripts/start_tomcat_if_present.sh" | tee -a /etc/crontab