#!/bin/bash

SRV_NAME=$1
SRV_PORT=$2

SRV_NAME_UNDERLINE=${SRV_NAME}

BAST_DIR_ESCAP=$(pwd);

./bin/start.sh ${SRV_NAME} ${SRV_PORT} ${BAST_DIR_ESCAP}

echo "all restart."


