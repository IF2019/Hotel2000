#!/usr/bin/env bash
ACCOUNT=$1
ACCOUNT=${ACCOUNT:=0}
THREAD=$2
THREAD=${THREAD:=1}

BLOCKCHAIN_NAME=bchotel2000
BLOCKCHAIN_DIR=blockchain

cd $( dirname "${BASH_SOURCE[0]}" )/../${BLOCKCHAIN_DIR}/${BLOCKCHAIN_NAME}

echo "geth --datadir=./ --rpcapi personal,db,eth,net,web3 --rpc --etherbase $ACCOUNT --minerthreads=$THREAD --mine console"

geth  --etherbase $ACCOUNT --datadir=./ --rpcapi personal,db,eth,net,web3 --rpc --minerthreads=$THREAD --mine console
