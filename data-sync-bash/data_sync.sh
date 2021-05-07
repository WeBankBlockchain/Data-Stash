#!/usr/bin/env sh

#determine the operating system and pull the corresponding execution file
if [ "$(uname)" == "Darwin" ]; then
    curl -#LO https://github.com/WeBankBlockchain/Data-Link/releases/download/V1.0.0/data-sync_mac.tar.gz
    tar -zxvf data-sync_mac.tar.gz
elif [ "$(expr substr $(uname -s) 1 5)" == "Linux" ]; then
    curl -#LO https://github.com/WeBankBlockchain/Data-Link/releases/download/V1.0.0/data-sync_linux.tar.gz
    tar -zxvf data-sync_linux.tar.gz
fi

# shellcheck disable=SC2164
cd data-sync
#read the param from config.conf and execute
CONFIGPATH="../config.conf"

STASH_IP=`awk -F '=' '/\[stash\]/{a=1}a==1&&$1~/stash.ip/{print $2;exit}' ${CONFIGPATH}`
STASH_PORT=`awk -F '=' '/\[stash\]/{a=1}a==1&&$1~/stash.port/{print $2;exit}' ${CONFIGPATH}`
STASH_DBNAME=`awk -F '=' '/\[stash\]/{a=1}a==1&&$1~/stash.dbname/{print $2;exit}' ${CONFIGPATH}`
STASH_USERNAME=`awk -F '=' '/\[stash\]/{a=1}a==1&&$1~/stash.username/{print $2;exit}' ${CONFIGPATH}`
STASH_PASSWORD=`awk -F '=' '/\[stash\]/{a=1}a==1&&$1~/stash.password/{print $2;exit}' ${CONFIGPATH}`
DATAPATH=`awk -F '=' '/\[data\]/{a=1}a==1&&$1~/sync.dataPath/{print $2;exit}' ${CONFIGPATH}`
ENDBLOCKNUMBER=`awk -F '=' '/\[more\]/{a=1}a==1&&$1~/sync.endBlockNumber/{print $2;exit}' ${CONFIGPATH}`
PAGECOUNT=`awk -F '=' '/\[more\]/{a=1}a==1&&$1~/sync.pageCount/{print $2;exit}' ${CONFIGPATH}`
TABLEPAGECOUNT=`awk -F '=' '/\[more\]/{a=1}a==1&&$1~/sync.bigTablePageCount/{print $2;exit}' ${CONFIGPATH}`
TYPE=`awk -F '=' '/\[data\]/{a=1}a==1&&$1~/sync.type/{print $2;exit}' ${CONFIGPATH}`

./fisco-sync -n ${ENDBLOCKNUMBER} -i ${STASH_IP} -t ${STASH_PORT} -d ${STASH_DBNAME} -u ${STASH_USERNAME} -p ${STASH_PASSWORD} -c ${DATAPATH} -l ${PAGECOUNT} -s ${TABLEPAGECOUNT} -e ${TYPE}



