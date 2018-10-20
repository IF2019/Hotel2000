#!/usr/bin/env bash

BLOCKCHAIN_NAME=bchotel2000
BLOCKCHAIN_DIR=blockchain

cd $( dirname "${BASH_SOURCE[0]}" )/../${BLOCKCHAIN_DIR}/${BLOCKCHAIN_NAME}

geth account new --datadir ./

echo
echo

echo "path=${BLOCKCHAIN_DIR}/${BLOCKCHAIN_NAME}/keystore/$(ls -t keystore/ | head -1)"
echo "address=$(ls -t keystore/ | head -1 | grep -o [a-z0-9]*$)"