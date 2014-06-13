#!/usr/bin/env bash
set -e

# set the BATCHERS_MASTER_IP to localhost if it is missing
if [ -z "$BATCHERS_MASTER_IP" ]; then export BATCHERS_MASTER_IP="127.0.0.1" ;fi

echo "BATCHERS_MASTER_IP is $BATCHERS_MASTER_IP"

# delete the line containing batchersmaster in /etc/hosts
sed -i '/batchersmaster/d' /etc/hosts
echo "setting batchersmaster to $BATCHERS_MASTER_IP in /etc/hosts"
echo "$BATCHERS_MASTER_IP     batchersmaster" | tee -a /etc/hosts
