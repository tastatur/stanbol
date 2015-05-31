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

echo ">> Building incoming links File <<"
MAX_SORT_MEM=5G

INCOMING_FILE=incoming_links.txt

#prpair Page_Links
PAGE_LINKS_FILE=rdfdata/dewiki-20140813-page-links.nt.gz

#prpair Redirects
REDIRECTS_FILE=rdfdata/dewiki-20140813-redirects.nt.gz

zcat ${PAGE_LINKS_FILE} \
| sed -e 's/.*dbpedia\.org\/resource\/\([^>]*\)> ./\1/' \
| sort -S $MAX_SORT_MEM \
| uniq -c  \
| sort -nr -S $MAX_SORT_MEM > $INCOMING_FILE

# Sort the incoming links on the entities, removing initial spaces added by uniq
cat $INCOMING_FILE \
    | sed 's/^\s*//' \
    | sort -k 2b,2 > incoming_links_sorted_k2.txt

mv $INCOMING_FILE original_incoming_links.txt

# Sort redirects
zcat ${REDIRECTS_FILE} | grep -v "^#" \
    | sed 's/.*dbpedia\.org\/resource\/\([^>]*\)>.*dbpedia\.org\/resource\/\([^>]*\)> ./\1 \2/' \
    | sort -k 2b,2 > redirects_sorted_k2.txt

# Join redirects with the original incoming links to assign the
# same ranking to redirects
join -j 2 -o 2.1 1.1 redirects_sorted_k2.txt incoming_links_sorted_k2.txt \
    > incoming_links_redirects.txt

# Merge the two files - maybe use sort merge?!
cat incoming_links_redirects.txt incoming_links_sorted_k2.txt \
    | sort -nr -S $MAX_SORT_MEM > $INCOMING_FILE

# WE ARE NOT REMOVING INTERMEDIATE FILES
# rm -f $WORKSPACE/incoming_links_sorted_k2.txt
# rm -f $WORKSPACE/redirects_sorted_k2.txt
# rm -f $WORKSPACE/incoming_links_redirects.txt
