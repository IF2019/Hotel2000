#!/usr/bin/env bash
ACCOUNT=$1
ACCOUNT=${ACCOUNT:=0}
THREAD=$2
THREAD=${THREAD:=4}

BLOCKCHAIN_NAME=bchotel2000
BLOCKCHAIN_DIR=blockchain

cd $( dirname "${BASH_SOURCE[0]}" )/../${BLOCKCHAIN_DIR}/${BLOCKCHAIN_NAME}

echo "geth --etherbase $ACCOUNT --minerthreads=$THREAD --mine --datadir ./"
geth --etherbase $ACCOUNT --minerthreads=$THREAD --mine --datadir ./