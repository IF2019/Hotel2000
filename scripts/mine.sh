#!/usr/bin/env bash

BLOCKCHAIN_NAME=bchotel2000
BLOCKCHAIN_DIR=blockchain

geth --etherbase 1 --minerthreads=1 --mine --datadir $( dirname "${BASH_SOURCE[0]}" )/../${BLOCKCHAIN_DIR}/${BLOCKCHAIN_NAME}