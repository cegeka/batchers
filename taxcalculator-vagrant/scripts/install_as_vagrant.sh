#!/usr/bin/env bash
set -e

echo "Installing nodejs"
./scripts/install_nodejs.sh
echo "Installing batchers app"
./scripts/install_batchers.sh
cd $HOME
echo "Installing tomcat"
./scripts/download_tomcat.sh
echo "Installing webservices"
./scripts/install_stubwebservice.sh
echo "Installing presentation"
./scripts/install_presentation.sh
