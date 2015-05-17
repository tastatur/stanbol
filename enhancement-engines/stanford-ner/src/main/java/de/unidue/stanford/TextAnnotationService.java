package de.unidue.stanford;

import edu.stanford.nlp.ling.CoreLabel;
import org.apache.stanbol.enhancer.servicesapi.ContentItem;

import java.util.List;

public interface TextAnnotationService {
    void populateTextAnnotations(List<List<CoreLabel>> extractedEntities, ContentItem ci);
}
