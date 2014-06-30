#!/usr/bin/env bash
set -e

MASTER_IP=`ifconfig eth0|grep "inet addr"| awk '{print $2}'| awk -F: '{print $2}'`
docker run -i -t -d -e "BATCHERS_MASTER_IP=$MASTER_IP" batchersslave
#this command will get the ip of the current machine and send it as a env variable to the running container
# docker run -i -t -d -e "BATCHERS_MASTER_IP=172.17.42.1" batchersslave
