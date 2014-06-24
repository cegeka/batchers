#!/usr/bin/env bash
set -e

rm -rf *.box

export MASTER_UUID="$(VBoxManage list vms|grep vagrant_master|sed 's/.*{//' | sed 's/}.*$//')"
vagrant package --base $MASTER_UUID --output boxmaster.box

export SLAVE_UUID="$(VBoxManage list vms|grep vagrant_slave|sed 's/.*{//' | sed 's/}.*$//')"
vagrant package --base $SLAVE_UUID --output boxslave.box

export STANDALONE_UUID="$(VBoxManage list vms|grep vagrant_default|sed 's/.*{//' | sed 's/}.*$//')"
vagrant package --base $STANDALONE_UUID --output boxstandalone.box