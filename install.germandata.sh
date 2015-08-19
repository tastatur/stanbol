#!/bin/bash


DATADIR=$1
echo "Hallo! Ich installiere für dich den Index von de.dbpedia.org und Modellen für TIGER und PIG Engine. Die Dateien sind für git leider zu gross. :("
mkdir -p stanbol/datafiles/

cp $DATADIR/dedbpedia.solrindex.zip stanbol/datafiles/
cp $DATADIR/tiger.bin stanbol/datafiles/
cp $DATADIR/pig.bin stanbol/datafiles/
cp $DATADIR/tiger-mt-model.dat stanbol/datafiles/
cp $DATADIR/pig-mt-model.dat stanbol/datafiles/

echo "Um die Installation abzuschliessen, starte bitte Stanbool, dann gehe auf http://localhost:8080/system/console/bundles und installiere Bundle org.apache.stanbol.data.site.dedbpedia-1.0.0.jar, den du in $DATADIR finden solltest"
