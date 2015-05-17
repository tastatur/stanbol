#!/bin/bash

mvn install:install-file -Dfile=lib/stanford-ner-resources.jar -DgroupId=edu.stanford.nlp -DartifactId=stanford-resources -Dversion=3.5.2 -Dpackaging=jar

mvn install:install-file -Dfile=lib/stanford-ner-3.5.2.jar -DgroupId=edu.stanford.nlp -DartifactId=stanford-ner -Dversion=3.5.2 -Dpackaging=jar
mvn install:install-file -Dfile=lib/stanford-ner-3.5.2-sources.jar -DgroupId=edu.stanford.nlp -DartifactId=stanford-ner -Dversion=3.5.2 -Dclassifier=sources -Dpackaging=jar
mvn install:install-file -Dfile=lib/stanford-ner-3.5.2-javadoc.jar -DgroupId=edu.stanford.nlp -DartifactId=stanford-ner -Dversion=3.5.2 -Dclassifier=javadoc -Dpackaging=jar