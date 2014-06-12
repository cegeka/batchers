#!/bin/bash
set -e

#move somewhere generic
export DISPLAY=:10

source $HOME/.bashrc

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

