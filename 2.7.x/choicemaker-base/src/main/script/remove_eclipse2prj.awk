#!/bin/awk
# 2016-07-05 rphall
#
# Removes eclipse2prj dependencies from a POM file
#
# Reads in a POM file from standard input,
# filters out the eclipse2prj dependencies,
# and prints the result to standard output.
#
# Usage:
#
# cat eclipse-prj_pom.xml | awk -f remove_eclipse2prj.awk | tee pom2.xml
#
/<dependency>/{
  dependency=1;
  count=0;
  line[++count]=$0;
  #print "line["count"]=" line[count];
  next;
}
/eclipse2prj/{
  eclipse2prj=1;
  line[++count]=$0;
  #print "line["count"]=" line[count];
  next;
}
/<\/dependency>/{
  line[++count]=$0;
  #print "line["count"]=" line[count];
  #print "eclipse2prj=" eclipse2prj
  min = 0;
  max = 0;
  if (eclipse2prj!=1) {
    for ( idx in line ) {
      if (idx+0 < min) min = idx;
      if (idx+0 > max) max = idx;
    }
    for (i=min; i<= max; i++) {
      if (i in line) {
        print line[i];
      }         
    }
  }
  delete line;
  dependency=0;
  eclipse2prj=0;
  next;
}
{
  if (dependency==1) {
    line[++count]=$0;
    #print "line["count"]=" line[count];
  } else {
    print $0;
  }
}

