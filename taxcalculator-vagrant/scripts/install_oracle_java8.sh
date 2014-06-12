#!/bin/bash
set -e

add-apt-repository ppa:webupd8team/java -y
echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections
sudo apt-get update
sudo apt-get install oracle-java8-installer -y
sudo apt-get install maven -y
sudo apt-get install oracle-java8-set-default -y