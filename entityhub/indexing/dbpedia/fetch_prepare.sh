#!/usr/bin/env bash

# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.


INDEXING_JAR=`pwd`/target/org.apache.stanbol.entityhub.indexing.dbpedia-0.12.1-SNAPSHOT.jar
WORKSPACE=~/tmp/dbpedia-index
DBPEDIA=http://downloads.dbpedia.org/3.9
MAX_SORT_MEM=2G

# Turn on echoing and exit on error
set -x -e -o pipefail

# Ensure that the workspace exists
mkdir -p $WORKSPACE

# Create the folder structure under the workspace folder
cd $WORKSPACE
java -jar $INDEXING_JAR init

# Rank entities by popularity by counting the number of incoming links in the
# wikipedia graph: computing this takes around 2 hours
if [ ! -f $WORKSPACE/indexing/resources/incoming_links.txt ]
then
    curl $DBPEDIA/de/page_links_de.nt.bz2 \
        | bzcat \
        | sed -e 's/.*<http\:\/\/dbpedia\.org\/resource\/\([^>]*\)> ./\1/' \
        | sed -e 's/CAT:/Category:/g' |
        | sort -S $MAX_SORT_MEM \
        | uniq -c  \
        | sort -nr -S $MAX_SORT_MEM > $WORKSPACE/indexing/resources/incoming_links.txt
fi

# Download the RDF dumps:
cd $WORKSPACE/indexing/resources/rdfdata

# General attributes for all entities
wget -c $DBPEDIA/dbpedia_3.9.owl.bz2
wget -c $DBPEDIA/de/instance_types_de.nt.bz2
wget -c $DBPEDIA/de/labels_de.nt.bz2
wget -c $DBPEDIA/de/short_abstracts_de.nt.bz2
wget -c $DBPEDIA/de/images_de.nt.bz2
wget -c $DBPEDIA/de/long_abstracts_de.not.bz2

# Type specific attributes
wget -c $DBPEDIA/de/geo_coordinates_de.nt.bz2
wget -c $DBPEDIA/de/persondata_de.nt.bz2

# Category information
wget -c $DBPEDIA/de/category_labels_de.nt.bz2
wget -c $DBPEDIA/de/skos_categories_de.nt.bz2
wget -c $DBPEDIA/de/article_categories_de.nt.bz2

# Redirects
wget -c $DBPEDIA/de/redirects_de.nt.bz2

set +xe

# Instruction to launch the indexing
echo "Preparation & data fetch done: edit config in $WORKSPACE/indexing/config/"
echo "Then launch indexing command:"
echo "(cd $WORKSPACE && java -jar $INDEXING_JAR index)"

