#!/usr/bin/env bash
ACCOUNT=$1
ACCOUNT=${ACCOUNT:=1}

BLOCKCHAIN_NAME=bchotel2000
BLOCKCHAIN_DIR=blockchain

cd $( dirname "${BASH_SOURCE[0]}" )/../${BLOCKCHAIN_DIR}/${BLOCKCHAIN_NAME}

echo "geth --datadir=./ --rpcapi personal,db,eth,net,web3 --rpc --etherbase $ACCOUNT --minerthreads=1 --mine console"

geth  --etherbase $ACCOUNT --datadir=./ --rpcapi personal,db,eth,net,web3 --rpc --minerthreads=1 --mine console
