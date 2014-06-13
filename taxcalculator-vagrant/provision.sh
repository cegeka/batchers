#!/usr/bin/env bash
set -e

export DEBIAN_FRONTEND=noninteractive
apt-get update && apt-get upgrade -y
apt-get install git software-properties-common -y

# delete the line containing taxcalculatordb etc/hosts
sed -i '/taxcalculatordb/d' /etc/hosts
# set master database ip address
echo "127.0.0.1     batchersmaster" | tee -a /etc/hosts

# make all files form scripts executable (windows removes this flag)
chmod +x scripts/*.sh

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
