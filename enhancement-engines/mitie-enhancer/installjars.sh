#!/bin/bash

mkdir lib
cp $1/javamitie.jar lib/
sudo cp $1/libjavamitie.so /usr/lib/
mvn install:install-file -Dfile=lib/javamitie.jar -DgroupId=edu.mit.mitie -DartifactId=mitie -Dversion=3.0 -Dpackaging=jar
