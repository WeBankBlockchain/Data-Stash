#!/usr/bin/env sh

#determine the operating system and pull the corresponding execution file
if [ "$(uname)" == "Darwin" ]; then
    curl -#LO https://github.com/WeBankBlockchain/Data-Stash/releases/download/V1.2.4/data-sync_mac.tar.gz
    tar -zxvf data-sync_mac.tar.gz
elif [ "$(expr substr $(uname -s) 1 5)" == "Linux" ]; then
    curl -#LO https://github.com/WeBankBlockchain/Data-Stash/releases/download/V1.2.4/data-sync_linux.tar.gz
    tar -zxvf data-sync_linux.tar.gz
fi

#read the param from config.conf and execute
CONFIGPATH="./config.conf"

STASH_IP=`awk -F '=' '/\[stash\]/{a=1}a==1&&$1~/stash.ip/{print $2;exit}' ${CONFIGPATH}`
STASH_PORT=`awk -F '=' '/\[stash\]/{a=1}a==1&&$1~/stash.port/{print $2;exit}' ${CONFIGPATH}`
STASH_DBNAME=`awk -F '=' '/\[stash\]/{a=1}a==1&&$1~/stash.dbname/{print $2;exit}' ${CONFIGPATH}`
STASH_USERNAME=`awk -F '=' '/\[stash\]/{a=1}a==1&&$1~/stash.username/{print $2;exit}' ${CONFIGPATH}`
STASH_PASSWORD=`awk -F '=' '/\[stash\]/{a=1}a==1&&$1~/stash.password/{print $2;exit}' ${CONFIGPATH}`
ENDBLOCKNUMBER=`awk -F '=' '/\[more\]/{a=1}a==1&&$1~/sync.endBlockNumber/{print $2;exit}' ${CONFIGPATH}`
PAGECOUNT=`awk -F '=' '/\[more\]/{a=1}a==1&&$1~/sync.pageCount/{print $2;exit}' ${CONFIGPATH}`
TABLEPAGECOUNT=`awk -F '=' '/\[more\]/{a=1}a==1&&$1~/sync.bigTablePageCount/{print $2;exit}' ${CONFIGPATH}`
GROUPID=`awk -F '=' '/\[node\]/{a=1}a==1&&$1~/node.groupId/{print $2;exit}' ${CONFIGPATH}`
NODEPATH=`awk -F '=' '/\[node\]/{a=1}a==1&&$1~/node.path/{print $2;exit}' ${CONFIGPATH}`

cd ..
current="`pwd`"

# shellcheck disable=SC2164
cd ${NODEPATH}
mv ${current}/data-sync-bash/data-sync/fisco-sync .

./fisco-sync -n ${ENDBLOCKNUMBER} -i ${STASH_IP} -t ${STASH_PORT} -d ${STASH_DBNAME} -u ${STASH_USERNAME} -p ${STASH_PASSWORD} -l ${PAGECOUNT} -s ${TABLEPAGECOUNT} -g ${GROUPID}

cd ${current}/data-sync-bash


