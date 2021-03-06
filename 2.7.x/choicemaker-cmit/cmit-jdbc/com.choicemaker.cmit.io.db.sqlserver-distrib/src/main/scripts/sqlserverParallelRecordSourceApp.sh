#!/bin/sh
#*******************************************************************************
# Copyright (c) 2003, 2018 ChoiceMaker LLC and others.
#
# This program and the accompanying materials
# are made available under the terms of the Eclipse Public License 2.0
# which accompanies this distribution, and is available at
# https://www.eclipse.org/legal/epl-2.0/
#
# SPDX-License-Identifier: EPL-2.0
#
# Contributors:
#     ChoiceMaker Technologies, Inc  - initial API and implementation
#     ChoiceMaker LLC - version 2.7 and following
#*******************************************************************************
#
# 2018-10-24 rphall
# Bash script to start the command-line Sqlserver RecordSourceSnapshot application.
# Tweak the JAVA variable to point at a 1.8 JRE or JDK.
# Earlier JVMs won't work, and later JVMs haven't been tested.
#
APP_DIR="`dirname "$0"`"
APP="com.choicemaker.cmit.io.db.sqlserver.SqlServerParallelRecordSourceApp"
LIB="$APP_DIR/lib"

# Java command
JAVA=/usr/bin/java

# Recommended memory allocations
#JAVA_OPTS="-Xms584M -Xmx512M"

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

# Uncomment the following line to specify the logging configuration
JAVA_OPTS="$JAVA_OPTS -Djava.util.logging.config.file=logging_local.properties"
JAVA_OPTS="$JAVA_OPTS -DpropertyFile=sqlserver_jdbc_local.properties"

# Uncomment the following line to enable assertions
JAVA_OPTS="$JAVA_OPTS -ea"

# Uncomment the following line to enable remote debugging
#JAVA_OPTS="$JAVA_OPTS -debug"
#JAVA_OPTS="$JAVA_OPTS -Xdebug -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=y"

CMD="$JAVA $JAVA_OPTS -cp $CP $APP $*"

#echo
#echo "$CMD"
#echo
eval "$CMD" 2>/dev/null
#echo

