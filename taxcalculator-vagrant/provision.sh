#!/usr/bin/env bash
set -e

#export DEBIAN_FRONTEND=noninteractive
#apt-get update && apt-get upgrade -y
#apt-get install git software-properties-common -y


# make all files form scripts executable (windows removes this flag)
chmod +x scripts/*.sh

echo "$USER BATCHERS_PROFILE is $BATCHERS_PROFILE"
echo "$USER BATCHERS_MASTER_IP is $BATCHERS_MASTER_IP"

./set_master_ip.sh

cd scripts
find ./ -type f -exec sed -i -e 's/^M$//' {} \;
find ./ -type f -exec sed -i -e $'s/\r$//' {} \;
cd ..

export PATH="/home/vagrant/.local/bin/karma:$PATH"

./scripts/install_xvfb_and_firefox.sh

./scripts/install_oracle_java8.sh

./scripts/install_mysql.sh

./scripts/install_rabbitmq.sh

#run script as Vagrant user not as root
sudo -i -H -u vagrant ./scripts/install_as_vagrant.sh