package de.unidue.stanbol.stanford;

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
        } else if (nerType.contains("MONEY")) {
            return new UriRef("http://dbpedia.org/ontology/Currency");
        } else if (nerType.contains("TIME")) {
            return new UriRef("http://schema.org/Time");
        } else if (nerType.contains("DATE")) {
            return new UriRef("http://schema.org/Date");
        } else if (nerType.contains("NUMBER")) {
            return new UriRef("http://schema.org/Number");
        } else {
            return new UriRef("http://www.w3.org/2002/07/owl#Thing");
        }
    }
}
