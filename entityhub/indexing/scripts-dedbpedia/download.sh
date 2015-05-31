#!/bin/bash

files=(
  http://de.dbpedia.org/downloads/20140813/dewiki-20140813-article-categories.nt.gz \
  http://de.dbpedia.org/downloads/20140813/dewiki-20140813-article-templates.nt.gz \
  http://de.dbpedia.org/downloads/20140813/dewiki-20140813-category-labels.nt.gz \
  http://de.dbpedia.org/downloads/20140813/dewiki-20140813-disambiguations.nt.gz \
  http://de.dbpedia.org/downloads/20140813/dewiki-20140813-external-links.nt.gz \
  http://de.dbpedia.org/downloads/20140813/dewiki-20140813-geo-coordinates.nt.gz \
  http://de.dbpedia.org/downloads/20140813/dewiki-20140813-homepages.nt.gz \
  http://de.dbpedia.org/downloads/20140813/dewiki-20140813-images.nt.gz \
  http://de.dbpedia.org/downloads/20140813/dewiki-20140813-infobox-properties.nt.gz \
  http://de.dbpedia.org/downloads/20140813/dewiki-20140813-infobox-test.nt.gz \
  http://de.dbpedia.org/downloads/20140813/dewiki-20140813-infobox-property-definitions.nt.gz \
  http://de.dbpedia.org/downloads/20140813/dewiki-20140813-instance-types.nt.gz \
  http://de.dbpedia.org/downloads/20140813/dewiki-20140813-interlanguage_links_de.nt.gz \
  http://de.dbpedia.org/downloads/20140813/dewiki-20140813-labels.nt.gz \
  http://de.dbpedia.org/downloads/20140813/dewiki-20140813-long-abstracts.nt.gz \
  http://de.dbpedia.org/downloads/20140813/dewiki-20140813-mappingbased-properties.nt.gz \
  http://de.dbpedia.org/downloads/20140813/dewiki-20140813-out-degree.nt.gz \
  http://de.dbpedia.org/downloads/20140813/dewiki-20140813-page-ids.nt.gz \
  http://de.dbpedia.org/downloads/20140813/dewiki-20140813-page-length.nt.gz \
  http://de.dbpedia.org/downloads/20140813/dewiki-20140813-page-links.nt.gz \
  http://de.dbpedia.org/downloads/20140813/dewiki-20140813-persondata.nt.gz \
  http://de.dbpedia.org/downloads/20140813/dewiki-20140813-pnd.nt.gz \
  http://de.dbpedia.org/downloads/20140813/dewiki-20140813-redirects.nt.gz \
  http://de.dbpedia.org/downloads/20140813/dewiki-20140813-revision-ids.nt.gz \
  http://de.dbpedia.org/downloads/20140813/dewiki-20140813-revision-uris.nt.gz \
  http://de.dbpedia.org/downloads/20140813/dewiki-20140813-short-abstracts.nt.gz \
  http://de.dbpedia.org/downloads/20140813/dewiki-20140813-skos-categories.nt.gz \
  http://de.dbpedia.org/downloads/20140813/dewiki-20140813-specific-mappingbased-properties.nt.gz \
  http://de.dbpedia.org/downloads/20140813/dewiki-20140813-wikipedia-links.nt.gz
)

for i in "${files[@]}"
do
    :
    # clean possible encoding errors
    filename=$(basename $i)
    if [ ! -f ${filename} ]
    then
        url=${i}
        wget -c ${url}
        echo "cleaning $filename ..."
        #corrects encoding
        zcat ${filename} \
            | sed 's/\\\\/\\u005c\\u005c/g;s/\\\([^u"]\)/\\u005c\1/g' \
            | gzip -c > ${filename}-new.gz
        rm -f ${filename}
        mv ${filename}-new.gz ${filename}
    fi
done
