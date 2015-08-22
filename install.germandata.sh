#!/bin/bash


DATADIR=$1
STANBOLDIR=$2
echo "Hallo! Ich installiere für dich den Index von de.dbpedia.org und Modellen für TIGER und PIG Engine. Die Dateien sind für git leider zu gross. :("
mkdir -p $STANBOLDIR/datafiles/

cp $DATADIR/dedbpedia.solrindex.zip $STANBOLDIR/datafiles/
cp $DATADIR/tiger.bin $STANBOLDIR/datafiles/
cp $DATADIR/pig.bin $STANBOLDIR/datafiles/
cp $DATADIR/tiger-mt-model.dat $STANBOLDIR/datafiles/
cp $DATADIR/pig-mt-model.dat $STANBOLDIR/datafiles/

echo "Um die Installation abzuschliessen, starte bitte Stanbool, dann gehe auf http://localhost:8080/system/console/bundles und installiere Bundle org.apache.$STANBOLDIR.data.site.dedbpedia-1.0.0.jar, den du in $DATADIR finden solltest"
