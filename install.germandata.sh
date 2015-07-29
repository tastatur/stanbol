#!/bin/bash


DATADIR=$1
echo "Hallo! Ich installiere für dich den Index von de.dbpedia.org und Modellen für TIGER und PIG Engine. Die Dateien sind für git leider zu gross. :("
mkdir -p stanbol/datafiles/

cp $DATADIR/dedbpedia.solrindex.zip stanbol/datafiles/
cp $DATADIR/tiger.bin stanbol/datafiles/
cp $DATADIR/pig.bin stanbol/datafiles/
