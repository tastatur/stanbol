package de.unidue.stanford;

import org.apache.clerezza.rdf.core.UriRef;
import org.apache.stanbol.enhancer.servicesapi.rdf.OntologicalClasses;

import java.util.function.Function;

public class StanfordNerTypeToDcType implements Function<String, UriRef> {
    @Override
    public UriRef apply(String nerType) {
        if (nerType.contains("PER")) {
            return OntologicalClasses.DBPEDIA_PERSON;
        } else if (nerType.contains("LOC")) {
            return OntologicalClasses.DBPEDIA_PLACE;
        } else if (nerType.contains("ORG")) {
            return OntologicalClasses.DBPEDIA_ORGANISATION;
        } else {
            return new UriRef("http://dbpedia.org/ontology/Thing");
        }
    }
}
