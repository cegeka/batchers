#!/usr/bin/env bash
set -e

# make all files form scripts executable (windows removes this flag)
chmod +x scripts/*.sh

cd scripts
find ./ -type f -exec sed -i -e 's/^M$//' {} \;
find ./ -type f -exec sed -i -e $'s/\r$//' {} \;
cd ..