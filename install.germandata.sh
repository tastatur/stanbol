#!/bin/bash


INSTALLDIR=$1

if [[ "$2" == "" ]]; then
  echo "Bitte, sage mir, ob du stanbol als eine WAR-Datei (-t) auf tomcat installieren, oder local(-l) starten möchtest"
  exit -1
fi

if [[ "$2" == "-t" ]]; then
   STANBOLDIR=/var/lib/stanbol-data/
   TOMCATUSER=tomcat8
elif [[ "$2" == "-l" ]]; then
   STANBOLDIR=$(pwd)/stanbol
fi
echo "Hallo! Ich installiere für dich den Index von de.dbpedia.org und Modellen für TIGER und PIG Engine. Die Dateien sind für git leider zu gross. :("
mkdir -p $STANBOLDIR/datafiles/

cp $INSTALLDIR/dedbpedia.solrindex.zip $STANBOLDIR/datafiles/
cp $INSTALLDIR/tiger.bin $STANBOLDIR/datafiles/
cp $INSTALLDIR/pig.bin $STANBOLDIR/datafiles/
cp $INSTALLDIR/tiger-mt-model.dat $STANBOLDIR/datafiles/
cp $INSTALLDIR/pig-mt-model.dat $STANBOLDIR/datafiles/

if [[ "$2" == "-t" ]]; then
   chown -R $TOMCATUSER:$TOMCATGROUP $STANBOLDIR
fi
echo "Um die Installation abzuschliessen, starte bitte Stanbool, dann gehe auf http://localhost:8080/system/console/bundles und installiere Bundle org.apache.$STANBOLDIR.data.site.dedbpedia-1.0.0.jar, den du in $INSTALLDIR finden solltest"
