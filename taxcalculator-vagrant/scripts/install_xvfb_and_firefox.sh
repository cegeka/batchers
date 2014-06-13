#!/usr/bin/env bash
set -e

# install firefox xvfb for e2e headless tests
apt-get install firefox xvfb -y

# configure display for e2e
export DISPLAY=:10

# find existing xvfb process
export XVFB_PID=$(pidof /usr/bin/Xvfb)
if [ -n "$XVFB_PID" ]; then
  echo "Xvfb already started"
else
  echo "Starting Xvfb"
  Xvfb :10 -ac </dev/null &>/dev/null &
fi;

echo "export DISPLAY=:10" | tee -a /home/vagrant/.bashrc
