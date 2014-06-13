#!/bin/bash
set -e

#move somewhere generic
export DISPLAY=:10
export PATH="/home/vagrant/.local/bin:/home/vagrant/.ndenv/shims:/home/vagrant/.ndenv/bin/:$PATH"

cd $HOME
rm -rf batchers
git clone https://github.com/cegeka/batchers.git
cd $HOME/batchers/taxcalculator
mvn clean install

cd taxcalculator-presentation
mvn package
#~/batchers/taxcalculator/taxcalculator-presentation/target/taxcalculator-presentation-1.0-SNAPSHOT.war

cd ../taxcalculator-stubwebservice/
mvn package

