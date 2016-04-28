#!/bin/sh
#
# Downloads and installs the startssl CA certs into the global java keystore
# Author: Klaus Reimer <k@ailis.de> -- Original Linux script
# Author: Rick Hall <rphall@choicemaker.com> -- Adapted to Git Bash shell on Windows
#
# See "K's cluttered loft", "Java and the StartSSL CA certificates"
# https://www.ailis.de/~k/archives/52-Java-and-the-StartSSL-CA-certificates.html
# http://links.rph.cx/1VQawa0
#

# Step through code
#  -- See StackOverflow "How to execute bash script line by line",
#  -- user 'organic-mashup', http:links.rph.cx/1VQhsnA
# trap "set +x; sleep 1; set -x" DEBUG

# Check if JRE is set
if [ "$JRE" = "" ]
then
    echo "ERROR: JRE must be set."
    exit 1
fi

KEYTOOL="$JRE/bin/keytool"

# Check if cacerts file is present
if [ ! -f "$JRE/lib/security/cacerts" ]
then
    echo "ERROR: \"\$JRE\"/lib/security/cacerts not found. Is JRE set correctly?"
    exit 1
fi

# Download the startssl certs
echo "Downloading certs..."
curl --silent http://www.startssl.com/certs/ca.crt > ca.crt
curl --silent http://www.startssl.com/certs/sub.class1.server.ca.crt > sub.class1.server.ca.crt
curl --silent http://www.startssl.com/certs/sub.class2.server.ca.crt > sub.class2.server.ca.crt
curl --silent http://www.startssl.com/certs/sub.class3.server.ca.crt > sub.class3.server.ca.crt
curl --silent http://www.startssl.com/certs/sub.class4.server.ca.crt > sub.class4.server.ca.crt

# Install certs into global keystore
echo "Adding certs to cacerts keystore ..."
"$KEYTOOL" -import -trustcacerts -keystore "$JRE/lib/security/cacerts" -storepass changeit -noprompt -alias startcom.ca -file ca.crt
"$KEYTOOL" -import -trustcacerts -keystore "$JRE/lib/security/cacerts" -storepass changeit -noprompt -alias startcom.ca.sub.class1 -file sub.class1.server.ca.crt
"$KEYTOOL" -import -trustcacerts -keystore "$JRE/lib/security/cacerts" -storepass changeit -noprompt -alias startcom.ca.sub.class2 -file sub.class2.server.ca.crt
"$KEYTOOL" -import -trustcacerts -keystore "$JRE/lib/security/cacerts" -storepass changeit -noprompt -alias startcom.ca.sub.class3 -file sub.class3.server.ca.crt
"$KEYTOOL" -import -trustcacerts -keystore "$JRE/lib/security/cacerts" -storepass changeit -noprompt -alias startcom.ca.sub.class4 -file sub.class4.server.ca.crt

# If jsse is installed then also put the certs into jssecacerts keystore
if [ -f "$JRE/lib/security/jssecacerts" ]
then
    echo "Adding certs to jssecacerts keystore ..."
    "$KEYTOOL" -import -trustcacerts -keystore "$JRE/lib/security/jssecacerts" -storepass changeit -noprompt -alias startcom.ca -file ca.crt
    "$KEYTOOL" -import -trustcacerts -keystore "$JRE/lib/security/jssecacerts" -storepass changeit -noprompt -alias startcom.ca.sub.class1 -file sub.class1.server.ca.crt
    "$KEYTOOL" -import -trustcacerts -keystore "$JRE/lib/security/jssecacerts" -storepass changeit -noprompt -alias startcom.ca.sub.class2 -file sub.class2.server.ca.crt
    "$KEYTOOL" -import -trustcacerts -keystore "$JRE/lib/security/jssecacerts" -storepass changeit -noprompt -alias startcom.ca.sub.class3 -file sub.class3.server.ca.crt
    "$KEYTOOL" -import -trustcacerts -keystore "$JRE/lib/security/jssecacerts" -storepass changeit -noprompt -alias startcom.ca.sub.class4 -file sub.class4.server.ca.crt
fi

# Remove downloaded certs
rm -f ca.crt sub.class1.server.ca.crt sub.class2.server.ca.crt sub.class3.server.ca.crt sub.class4.server.ca.crt

