#!/usr/bin/env bash

BLOCKCHAIN_NAME=bchotel2000
BLOCKCHAIN_DIR=blockchain

ACCOUNT_PASSWORD=$1
ACCOUNT_NAME=$2
cd $( dirname "${BASH_SOURCE[0]}" )/../${BLOCKCHAIN_DIR}/${BLOCKCHAIN_NAME}
if [ -n "$ACCOUNT_PASSWORD" ]; then
    geth account new --datadir ./ --password <(echo "${ACCOUNT_PASSWORD}")
else
    geth account new --datadir ./
fi

ACCOUNT_PATH="${BLOCKCHAIN_DIR}/${BLOCKCHAIN_NAME}/keystore/$(ls -t keystore/ | head -1)"
ACCOUNT_ADDRESS="$(ls -t keystore/ | head -1 | grep -o [a-z0-9]*$)"
echo "path=${ACCOUNT_PATH}"
echo "address=${ACCOUNT_ADDRESS}"

if [ -n "$ACCOUNT_NAME" ]; then
    cd ../../src/main/resources/
    cat >>config-local.properties << ENDFILE

hotel2000.account.${ACCOUNT_NAME}.path=${ACCOUNT_PATH}
hotel2000.account.${ACCOUNT_NAME}.address=${ACCOUNT_ADDRESS}
hotel2000.account.${ACCOUNT_NAME}.passphrase=${ACCOUNT_PASSWORD}
ENDFILE
fi
