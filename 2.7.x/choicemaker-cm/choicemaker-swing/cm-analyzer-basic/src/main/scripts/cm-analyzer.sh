#!/bin/sh
#
# 2014-11-29 rphall
# Bash script to start CM Analyzer as an embedded E2 application.
# Tweak the JAVA variable to point at a 1.7 JRE or JDK.
# Earlier JVMs won't work, and later JVMs haven't been tested.
# See http://java.sun.com/products/archive
#
APP_DIR="`dirname "$0"`"
LIB_DIR="$APP_DIR/lib"
APP="com.choicemaker.cm.modelmaker.app.ModelMakerApp"

# Java command (Java 1.7 is required)
#JAVA="/usr/java/jdk1.7.0_55/bin/java"
JAVA=${JAVA_HOME}/bin/java

# Recommended memory allocations
JAVA_OPTS="-Xms384M -Xmx512M"

# Logging configuration
JAVA_OPTS="$JAVA_OPTS -Djava.util.logging.config.file=logging.properties"

# Embedded E2 settings
JAVA_OPTS="$JAVA_OPTS -DcmInstallablePlatform=com.choicemaker.e2.embed.EmbeddedPlatform"
JAVA_OPTS="$JAVA_OPTS -DcmInstallableConfigurator=com.choicemaker.cm.core.xmlconf.XmlConfigurator"

# Uncomment the following lines to enable remote debugging
#JAVA_OPTS="$JAVA_OPTS -Xdebug -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=n"

# Compute, display and evaluate the command to start CM Analyzer
CMD="$JAVA $JAVA_OPTS -cp \"$LIB_DIR/*\" $APP"
echo "$CMD"
eval "$CMD"

