#!/bin/bash
set -e

add-apt-repository ppa:webupd8team/java -y
# accept java license with no user interaction
echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections
sudo apt-get update
sudo apt-get install oracle-java8-installer -y
# do not install maven before java because it will install openjdk7
sudo apt-get install maven -y
# set java8 as default
sudo apt-get install oracle-java8-set-default -y
