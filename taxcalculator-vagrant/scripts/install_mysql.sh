#!/bin/bash
set -e

#set password for root user without user interaction
debconf-set-selections <<< 'mysql-server mysql-server/root_password password taxcalculator'
debconf-set-selections <<< 'mysql-server mysql-server/root_password_again password taxcalculator'
apt-get install mysql-server-5.5 -y

mysql --user=root --password=taxcalculator -e "drop database if exists taxcalculator;"
mysql --user=root --password=taxcalculator -e "create database taxcalculator;"
mysql --user=root --password=taxcalculator -e "grant all on *.* to 'taxcalculator'@'%' identified by 'taxcalculator';"

# backup my.cnf
cp /etc/mysql/my.cnf /etc/mysql/my.cnf.original
# listen on all network interfaces not only on localhost
sed -i 's/bind-address.*/bind-address            = 0.0.0.0/g' /etc/mysql/my.cnf
service mysql restart
