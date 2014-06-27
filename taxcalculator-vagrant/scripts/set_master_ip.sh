#!/usr/bin/env bash
set -e

# set the BATCHERS_MASTER_IP to localhost if it is missing
if [ -z "$BATCHERS_MASTER_IP" ]; then export BATCHERS_MASTER_IP="127.0.0.1" ;fi

echo "BATCHERS_MASTER_IP is $BATCHERS_MASTER_IP"

# docker uses /tmp/hosts
HOSTS_FILE="/etc/hosts"
if [ -f /tmp/hosts ]; then
  HOSTS_FILE="/tmp/hosts"
fi

# delete the line containing batchersmaster in $HOSTS_FILE
sed -i '/batchersmaster/d' $HOSTS_FILE
echo "setting batchersmaster to $BATCHERS_MASTER_IP in $HOSTS_FILE"
echo "$BATCHERS_MASTER_IP     batchersmaster" | tee -a $HOSTS_FILE
