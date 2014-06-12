#!/bin/bash
set -e

# use ndenv to freeze nodejs version
rm -rf ~/.ndenv
git clone https://github.com/riywo/ndenv ~/.ndenv
echo 'export PATH="$HOME/.ndenv/bin:$HOME/.ndenv/shims:$PATH"' >> ~/.bashrc
echo 'eval "$(ndenv init -)"' >> ~/.bashrc

export PATH='$HOME/.ndenv/bin:$HOME/.ndenv/shims:$PATH'

git clone https://github.com/riywo/node-build.git ~/.ndenv/plugins/node-build
ndenv install v0.10.28
ndenv global v0.10.28
ndenv rehash

echo 'export PATH="$HOME/.local/bin:$PATH"' >> ~/.bashrc
export PATH="$HOME/.local/bin:$PATH"

rm -rf ~/.local/lib/node_modules
echo "prefix = ~/.local
root = ~/.local/lib/node_modules
binroot = ~/.local/bin
manroot = ~/.local/share/man" | tee ~/.npmrc


echo "$PATH"
npm install -g karma karma-ng-scenario karma-junit-reporter karma-jasmine
npm install karma-chrome-launcher --save-dev
npm install karma-firefox-launcher -g
npm install karma-jasmine --save-dev


rm -rf ~/.local/bin/karma
mkdir -p ~/.local/bin/
ln -s ~/.local/lib/node_modules/karma/bin/karma ~/.local/bin/karma
