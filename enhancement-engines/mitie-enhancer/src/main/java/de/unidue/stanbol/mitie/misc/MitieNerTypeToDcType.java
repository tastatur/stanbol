package de.unidue.stanbol.mitie.misc;

import javafx.beans.binding.Bindings;
import org.apache.clerezza.rdf.core.UriRef;

import java.util.function.Function;

public class MitieNerTypeToDcType implements Function<String, UriRef> {
    private static final String DBPEDIA_ONTOLOGY_PREFIX = "http://dbpedia.org/ontology/";

    @Override
    public UriRef apply(String nerType) {
        if (nerType.equalsIgnoreCase("misc")) {
            return new UriRef("http://www.w3.org/2002/07/owl#Thing");
        } else {
            return new UriRef(DBPEDIA_ONTOLOGY_PREFIX.concat(nerType.toLowerCase()));
        }
    }
}
