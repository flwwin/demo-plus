#!/bin/bash

SRV_NAME=$1
SRV_PORT=$2

## 查询pid
cmd="ps auxf | grep '\-jar' | grep "${SRV_NAME}" | grep "${SRV_PORT}" | grep -v bash | awk '{print \$2}'"
echo ${cmd}
pid=`eval $cmd`;

if [[ -n ${pid} ]]; then
    kill "${pid}"
    echo "kill ${pid}"
else
    exit 0
fi

## 等待 5 秒 优雅停机失败, 强制停止
sleep 5
pid=`eval $cmd`;
if [[ -n ${pid} ]]; then
    kill -9 "${pid}"
    echo "kill -9 ${pid}"
fi
