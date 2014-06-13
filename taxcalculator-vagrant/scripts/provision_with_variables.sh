#!/usr/bin/env bash
set -e

echo "$USER BATCHERS_PROFILE is $BATCHERS_PROFILE"
echo "$USER BATCHERS_MASTER_IP is $BATCHERS_MASTER_IP"

export DEBIAN_FRONTEND=noninteractive
apt-get update && apt-get upgrade -y
apt-get install git software-properties-common -y

./scripts/set_master_ip.sh

export PATH="/home/vagrant/.local/bin/karma:$PATH"

./scripts/install_xvfb_and_firefox.sh

./scripts/install_oracle_java8.sh

./scripts/install_mysql.sh

./scripts/install_rabbitmq.sh

#run script as Vagrant user not as root
sudo -i -H -u vagrant ./scripts/install_as_vagrant.sh

sed -i '/start_tomcat_if_present/d' /etc/crontab
echo "@reboot vagrant /home/vagrant/scripts/start_tomcat_if_present.sh" | tee -a /etc/crontab