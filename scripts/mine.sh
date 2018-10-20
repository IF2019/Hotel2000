#!/usr/bin/env bash
ACCOUNT=$1
ACCOUNT=${ACCOUNT:=1}

BLOCKCHAIN_NAME=bchotel2000
BLOCKCHAIN_DIR=blockchain

cd $( dirname "${BASH_SOURCE[0]}" )/../${BLOCKCHAIN_DIR}/${BLOCKCHAIN_NAME}

echo "geth --etherbase $ACCOUNT --minerthreads=4 --mine --datadir ./"
geth --etherbase $ACCOUNT --minerthreads=4 --mine --datadir ./