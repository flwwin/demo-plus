 #!/bin/bash

source $(dirname $0)/../../env.sh

U=`id`

JAR_NAME="${project.artifactId}-${project.version}.${project.packaging}"

SRV_NAME=$1
SRV_PORT=$2
BAST_DIR=$3
export paas_prometheus_port=$(( $2 + 1007 ))

echo "--------------------"
echo "ENV=${ENV}"
echo "--------------------"
echo "SHELL USER=${U}"

SRV_NAME_UNDERLINE=${SRV_NAME//-/_}

echo "SRV_NAME_UNDERLINE:${SRV_NAME_UNDERLINE}"
# 检查服务名和端口--------------------
if [[ -z "$SRV_NAME" || -z "$SRV_PORT" ]]; then
  echo "ERROR !!! empty SRV_NAME or empty SRV_PORT !!!"
  exit -1;
fi

cmd="ps auxf | grep '\-jar' | grep "${SRV_NAME}" | grep "${SRV_PORT}" | grep -v bash | awk '{print \$2}'"

pid=`eval $cmd`;
if [[ ! -z ${pid} ]]; then
  echo "WARN ! service has started !";
  exit -1;
fi


# java版本相关 如果是java11 后缀="_11"--------------------
JAVA_VERSION_SUFFIX=""
VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f 2)
MAIN_VERSION=$(echo ${VERSION}|cut -d'.' -f 1);
if [[ "${MAIN_VERSION}" == "11" ]]; then
    JAVA_VERSION=${MAIN_VERSION}
    JAVA_VERSION_SUFFIX="_${MAIN_VERSION}"
else
    JAVA_VERSION=$(echo ${VERSION}|cut -d'.' -f 2);
fi

echo "JAVA_VERSION: ${JAVA_VERSION}"



# jvm内存level 默认middle--------------------
# 拼凑内存变量,根据java版本不同带不同后缀
eval MEM_OPT_LEVEL=\$${SRV_NAME_UNDERLINE}${SRV_PORT}_MEM_OPTS
echo "MEM_OPT_LEVEL: ${MEM_OPT_LEVEL}"
if [[ ${MEM_OPT_LEVEL} = "" ]]; then
    MEM_OPT_LEVEL=${DEFAULT_MEM_OPTS}
fi
eval MEM_OPTS=\$MEM_OPTS_${MEM_OPT_LEVEL}${JAVA_VERSION_SUFFIX}

# 应用参数,其中dbType移动到OTHER_OPTS中--------------------
ENV_OPTS="-Denv=$ENV -Dapp=$SRV_NAME -Ddubbo.application.name=$SRV_NAME -Ddubbo.protocol.port=$SRV_PORT -Dinstance_id=$instance_id "

# jvm参数,根据java版本不同带不同后缀--------------------
eval JAVA_OPTS=\$JAVA_OPTS${JAVA_VERSION_SUFFIX}

# gc_log参数,根据java版本不同带不同后缀,替换其中的SRN_NAME和SRV_PORT--------------------
eval GC_OPTS=\$GC_OPTS${JAVA_VERSION_SUFFIX}
GC_OPTS=${GC_OPTS//SRV_NAME/$instance_id}
GC_OPTS=${GC_OPTS//SRV_PORT/$SRV_PORT}

# 其他关键参数--------------------
eval OTHER_OPTS=\$${SRV_NAME_UNDERLINE}${SRV_PORT}_OTHER_OPTS
# 其他关键参数中的默认参数--------------------
for key_value in "${DEFAULT_OTHER_OPTS_KEY_VALUE[@]}"
do
  key_value_pair=(${key_value//':'/' '})
  if [[ ${OTHER_OPTS} != *"-D${key_value_pair[0]}"* ]]; then
    eval DEFAULT_OTHER_OPTS_VALUE=\$${key_value_pair[1]}
    OTHER_OPTS="${OTHER_OPTS} -D${key_value_pair[0]}=${DEFAULT_OTHER_OPTS_VALUE}"
  fi
done


# 参数检查--------------------
echo "--------------------"
echo "MEM_OPTS=${MEM_OPTS}"
echo "--------------------"
echo "JAVA_OPTS=${JAVA_OPTS}"
echo "--------------------"
echo "GC_OPTS=${GC_OPTS}"
echo "--------------------"
echo "OTHER_OPTS=${OTHER_OPTS}"
echo "--------------------"
if [[ -z ${MEM_OPTS}  || -z ${JAVA_OPTS} || -z ${GC_OPTS} ]]; then
    echo "ERROR !!! empty MEM_OPTS or JAVA_OPTS or GC_OPTS !!!"
    exit -1
fi

# gc 日志路径

GCDIR=`echo ${GC_OPTS}|sed 's/.*Xlog.*file=\(.*\)\/gc-.*/\1/g'`
echo "GCDIR=${GCDIR}"
mkdir -p ${GCDIR}

# java 链接--------------------
BIN=${JAVA_HOME}/bin/java
LINK=${BAST_DIR}/bin/${SRV_NAME}${SRV_PORT}
if [[ ! -f ${LINK} ]]; then
	ln -s ${BIN} ${LINK};
fi
PROC_NAME="bin/./$SRV_NAME$SRV_PORT"


DEBUG_PORT=$((SRV_PORT+10000))
DEBUG_OPTS=${DEBUG_OPTS//DEBUG_PORT/$DEBUG_PORT}

DEV_NULL=">/dev/null 2>&1"
if [[ ${NOT_DEV_NULL} = "true" ]]; then
  DEV_NULL=""
fi

#执行--------------------
cd ${BAST_DIR}
EXEC="nohup $PROC_NAME $MEM_OPTS $JAVA_OPTS $GC_OPTS $ENV_OPTS $DEBUG_OPTS $OTHER_OPTS -jar $JAR_NAME $DEV_NULL &"
echo ${EXEC}
eval ${EXEC}

sleep 3

exit 0;

