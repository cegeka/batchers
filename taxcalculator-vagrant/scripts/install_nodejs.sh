#!/bin/bash
set -e

# use ndenv to freeze nodejs version
rm -rf $HOME/.ndenv
git clone https://github.com/riywo/ndenv $HOME/.ndenv
echo 'export PATH="$HOME/.ndenv/bin:$HOME/.ndenv/shims:$HOME/.local/bin:$PATH"' >> $HOME/.bashrc
echo 'eval "$(ndenv init -)"' >> $HOME/.bashrc

export PATH="$HOME/.ndenv/bin:$HOME/.ndenv/shims:$HOME/.local/bin:$PATH"

git clone https://github.com/riywo/node-build.git $HOME/.ndenv/plugins/node-build
ndenv install v0.10.28
ndenv global v0.10.28
ndenv rehash

echo 'export PATH="$HOME/.local/bin:$PATH"' >> $HOME/.bashrc
export PATH="$HOME/.local/bin:$PATH"

rm -rf $HOME/.local/lib/node_modules
echo "prefix = ~/.local
root = ~/.local/lib/node_modules
binroot = ~/.local/bin
manroot = ~/.local/share/man" | tee $HOME/.npmrc

npm install -g karma karma-ng-scenario karma-junit-reporter karma-jasmine
npm install karma-chrome-launcher --save-dev
npm install karma-firefox-launcher -g
npm install karma-jasmine --save-dev


rm -rf $HOME/.local/bin/karma
mkdir -p $HOME/.local/bin/
ln -s $HOME/.local/lib/node_modules/karma/bin/karma $HOME/.local/bin/karma
