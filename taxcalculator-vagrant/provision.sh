#!/usr/bin/env bash
set -e

export DEBIAN_FRONTEND=noninteractive
apt-get update && apt-get upgrade -y
apt-get install git -y

#set master database ip address
sed -i '/taxcalculatordb/d' /etc/hosts
echo "127.0.0.1     taxcalculatordb" | tee -a /etc/hosts

chmod +x scripts/*.sh

./scripts/install_xvfb_and_firefox.sh

./scripts/install_oracle_java8.sh

./scripts/install_mysql.sh

./scripts/install_rabbitmq.sh

#run script as Vagrant user not as root
su -c "./scripts/install_as_vagrant.sh" -s /bin/bash vagrant