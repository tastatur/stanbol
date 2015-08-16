#!/bin/bash

export LD_LIBRARY_PATH=$HOME/lib/
if [[ "$1" == "-d" ]]; then
 java -Xdebug -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=n -Xmx4g -jar launchers/full/target/org.apache.stanbol.launchers.full-0.12.1-SNAPSHOT.jar
else
 java -Xmx4g -jar launchers/full/target/org.apache.stanbol.launchers.full-0.12.1-SNAPSHOT.jar
fi
