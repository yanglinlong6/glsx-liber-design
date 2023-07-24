#!/usr/bin/env bash

time=`date +%Y%m%d`

cd `dirname $0`
BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`

LOGS_DIR=""
if [ -n "$LOGS_FILE" ]; then
    LOGS_DIR=`dirname $LOGS_FILE`
else
    LOGS_DIR=$DEPLOY_DIR/logs
fi
if [ ! -d $LOGS_DIR ]; then
    mkdir $LOGS_DIR
fi
STDOUT_FILE=$LOGS_DIR/server.$time.log

LIB_DIR=$DEPLOY_DIR/lib
LIB_JARS=`ls $LIB_DIR|grep .jar|awk '{print "'$LIB_DIR'/"$0}'|tr "\n" ":"`

JAVA_OPTS="-server -Xmx4g -Xms2g -Xss256k -XX:PermSize=128m -XX:+UseConcMarkSweepGC -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70"

CONF_DIR=$DEPLOY_DIR/conf
nohup java $JAVA_OPTS -classpath $CONF_DIR:$LIB_JARS com.glsx.cmpt.hbase.ActiveMain > $STDOUT_FILE 2>&1 &
echo "OK!"
