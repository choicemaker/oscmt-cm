#*******************************************************************************
# Copyright (c) 2015 ChoiceMaker LLC and others.
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
#*******************************************************************************
#!/bin/sh
BASE="`dirname $0`"
POM_DIR="$BASE/../../.."
POM="$POM_DIR/pom.xml"
echo
echo "   BASE: $BASE"
echo "POM_DIR: $POM_DIR"
echo "    POM: $POM"
if [ -f "$POM" ] ;
then
 echo 
 start="`date`" 
 for p in arquillian-jbossas-remote 
 #for p in arquillian-glassfish-remote
 do
  date 
  echo $p 
  time mvn clean test -P $p 2>/tmp/${p}.err \
    | tee /tmp/${p}.log \
    | grep -A 1 "Tests in error\|Tests run" 
  echo  
 done 
 finish="`date`" 
 echo " Start: $start"
 echo "Finish: $finish"
 echo
else
 echo "Missing file: $POM"
fi
