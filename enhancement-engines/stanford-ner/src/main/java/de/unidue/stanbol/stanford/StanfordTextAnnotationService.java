package de.unidue.stanbol.stanford;

import edu.stanford.nlp.ling.CoreLabel;
import org.apache.stanbol.enhancer.servicesapi.ContentItem;
import org.apache.stanbol.enhancer.servicesapi.EnhancementEngine;

import java.util.List;

public interface StanfordTextAnnotationService {
    void populateTextAnnotations(List<CoreLabel> extractedEntities, ContentItem ci, EnhancementEngine engine);
}
