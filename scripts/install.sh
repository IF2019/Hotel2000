#!/usr/bin/env bash
# run this in sudo mode: sudo bash ./01-install.sh

apt-get update
apt-get install -y software-properties-common
add-apt-repository -y ppa:ethereum/ethereum
apt-get update
apt-get install -y ethereum
apt-get install -y solc