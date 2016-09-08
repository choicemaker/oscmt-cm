#!/bin/sh
#
# 2014-10-20 rphall
# Bash script to start CM Analyzer as an Eclipse2 application.
# Tweak the JAVA variable to point at a 1.7 JRE or JDK.
# Earlier JVMs won't work, and later JVMs haven't been tested.
# See http://java.sun.com/products/archive
#
APP_DIR="`dirname "$0"`"
APP="com.choicemaker.cmtblocking.MainUnionLiteral"
LIB="$APP_DIR/lib"

# Java command
JAVA=/usr/bin/java

# Recommended memory allocations
JAVA_OPTS="-Xms384M -Xmx512M"

# Compute the class path
CP=""
let count=0
for f in $LIB/* ; do
  let count+=1
  if [ $count -eq 1 ]
  then
    CP="$f"
  else
    CP="$CP:$f"
  fi
done

# Uncomment the following line to enable remote debugging
#JAVA_OPTS="$JAVA_OPTS -debug"
#JAVA_OPTS="$JAVA_OPTS -Xdebug -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=y"

# Parse the command line
#if [[ $# == 0 ]]; then
#	echo
#	echo "Usage: $0 <conf-file>" 2>&1
#	echo
#  exit 1
#elif [[ $# == 1 ]]
#then
#	CONF="$1"
#else
#	echo
#	echo "Usage: $0 <conf-file>" 2>&1
#	echo
#  exit 1
#fi
#
#if [ ! -f "$CONF" ]
#then
#  echo
#  echo "ERROR: Configuration file '"$CONF"' doesn't exit or isn't a file." 1>&2
#  echo
#  exit 1
#fi

#echo
#echo "Using configuration file: '"$CONF"'"
#if [[ $# > 2 ]]; then
#	echo "Ignoring extra arguments"
#fi

CMD="$JAVA $JAVA_OPTS -cp $CP $APP $*"

echo
echo "$CMD"
echo
eval "$CMD"

