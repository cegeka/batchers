#!/usr/bin/env bash
set -e

apt-get install firefox xvfb -y

# configure display for e2e
export DISPLAY=:10

XCFB_PID=`ps ax|grep Xvfb|cut -d' ' -f1`
if [[ ! -n "$XCFB_PID" ]]; then
  echo "Starting Xvfb"
  Xvfb :10 -ac </dev/null &>/dev/null &
fi;
echo "export DISPLAY=:10" | tee -a /home/vagrant/.bashrc
