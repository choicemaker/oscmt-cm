#!/bin/sh
#
APP_DIR="`dirname "$0"`"
APP="com.choicemaker.cm.urm.client.app.UrmBmaApp"
LIB="$APP_DIR/lib"

# Java command
#JAVA=/usr/bin/java
JAVA=java

# Recommended memory allocations
JAVA_OPTS="-Xms384M -Xmx512M"

# Compute the class path
CP=""
let count=0
for f in $LIB/* ; do
  let count+=1
  if [ $count -eq 1 ]
  then
    CP="$APP_DIR/:$f"
  else
    CP="$CP:$f"
  fi
done

# Uncomment the following line to specify the logging configuration
JAVA_OPTS="$JAVA_OPTS -Djava.util.logging.config.file=$APP_DIR/logging.properties"

# Uncomment the following line to enable assertions
JAVA_OPTS="$JAVA_OPTS -ea"

# Uncomment the following line to enable remote debugging
JAVA_OPTS="$JAVA_OPTS -debug"
JAVA_OPTS="$JAVA_OPTS -Xdebug -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=n"

CMD="$JAVA $JAVA_OPTS -cp $CP $APP $*"

echo
echo "$CMD"
echo
eval "$CMD"

