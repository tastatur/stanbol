#!/bin/bash


INSTALLDIR=$1
STANBOLDIR=/var/lib/stanbol-data/
echo "Hallo! Ich installiere für dich den Index von de.dbpedia.org und Modellen für TIGER und PIG Engine. Die Dateien sind für git leider zu gross. :("
mkdir -p $STANBOLDIR/datafiles/

cp $INSTALLDIR/dedbpedia.solrindex.zip $STANBOLDIR/datafiles/
cp $INSTALLDIR/tiger.bin $STANBOLDIR/datafiles/
cp $INSTALLDIR/pig.bin $STANBOLDIR/datafiles/
cp $INSTALLDIR/tiger-mt-model.dat $STANBOLDIR/datafiles/
cp $INSTALLDIR/pig-mt-model.dat $STANBOLDIR/datafiles/

chown -R $TOMCATUSER:$TOMCATGROUP $STANBOLDIR
echo "Um die Installation abzuschliessen, starte bitte Stanbool, dann gehe auf http://localhost:8080/system/console/bundles und installiere Bundle org.apache.$STANBOLDIR.data.site.dedbpedia-1.0.0.jar, den du in $INSTALLDIR finden solltest"
