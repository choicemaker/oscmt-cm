#!/bin/sh
#*******************************************************************************
# Copyright (c) 2003, 2016 ChoiceMaker LLC and others.
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

#JAVA="$JAVA_HOME/bin/java"
#JAVA="/mnt/sda5/usr/java/j2sdk1.4.2_07/bin/java"
JAVA="/usr/java/j2sdk1.4.2_19/bin/java"

JAVA_OPTS="-Xms384M -Xmx512M"
#JAVA_OPTS="$JAVA_OPTS -Xdebug -Xrunjdwp:transport=dt_socket,address=28787,server=y,suspend=y"

CONF="projects/mci/project_OABA.xml"

BASEDIR="`pwd`"
#BASEDIR="$HOME/choicemaker_analyzer_cdss-mci_20090916_RC16_02

PARAM_DIR="$BASEDIR"
PARAMS="urmMatching.properties"

REPORT_DIR="$BASEDIR"
REPORT="Report.2009-09-01a.xml"

LOG4J_DIR="$BASEDIR"
LOG4J="log4j.properties"
JAVA_OPTS="$JAVA_OPTS -Dlog4j.configuration=file:$LOG4J_DIR/$LOG4J"

JNDI_OPTS="-Djava.naming.factory.initial=org.jnp.interfaces.NamingContextFactory"
JNDI_OPTS="$JNDI_OPTS -Djava.naming.provider.url=localhost:1099"
JNDI_OPTS="$JNDI_OPTS -Djava.naming.factory.url.pkgs=org.jboss.naming:org.jnp.interfaces"
JAVA_OPTS="$JAVA_OPTS $JNDI_OPTS"

cmd="$JAVA $JAVA_OPTS -cp startup.jar org.eclipse.core.launcher.Main \
  -application test.choicemaker.cm.online.urm.UrmOnlineTest \
  -conf $CONF \
  -matchParams $PARAM_DIR/$PARAMS \
  -report $REPORT_DIR/$REPORT \
  -guiErrorMessages"

echo
date
echo $cmd
echo
time eval $cmd
echo

