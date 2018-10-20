#!/usr/bin/env bash

BLOCKCHAIN_NAME=bchotel2000
BLOCKCHAIN_DIR=blockchain

cd $( dirname "${BASH_SOURCE[0]}" )/.. # Go to project dir

if [ -d "./${BLOCKCHAIN_DIR}" ]; then
    echo "${BLOCKCHAIN_DIR} exist déjà"
#    exit
    rm -rf ${BLOCKCHAIN_DIR}
fi

mkdir ${BLOCKCHAIN_DIR}
cd ${BLOCKCHAIN_DIR}

cat >genesis.json << ENDFILE
{
    "alloc": {},
    "coinbase": "0x0000000000000000000000000000000000000000",
    "config": {
    "chainId": 1608199012345,
    "eip155Block": 0,
    "eip158Block": 0,
    "homesteadBlock": 0
},
    "difficulty": "0x20000",
    "extraData": "",
    "gasLimit": "0x2fefd8",
    "mixhash": "0x0000000000000000000000000000000000000000000000000000000000000000",
    "nonce": "0x0000000000000042",
    "parentHash": "0x0000000000000000000000000000000000000000000000000000000000000000",
    "timestamp": "0x00"
}
ENDFILE

geth --datadir="${BLOCKCHAIN_NAME}" init genesis.json
