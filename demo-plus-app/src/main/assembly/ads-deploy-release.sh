#!/bin/bash

BAST_DIR=$(cd "$(dirname "$0")";pwd)
cd ${BAST_DIR}

SRV_NAME=$1
SRV_PORT=$2


if [[ -z ${SRV_NAME} || -z ${SRV_PORT} ]]; then
    SRV_NAME="ad-show-frequence-record-service";
    SRV_PORT="26250";
    # echo "ERROR !!! empty SRV_NAME or empty PORT !!!"
    # exit -1
fi

./bin/deploy.sh ${SRV_NAME} ${SRV_PORT}


