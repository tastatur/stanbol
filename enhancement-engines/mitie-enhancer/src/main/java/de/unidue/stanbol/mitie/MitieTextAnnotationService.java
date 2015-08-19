package de.unidue.stanbol.mitie;

import de.unidue.stanbol.mitie.impl.MitieEnhancementEngine;
import edu.mit.ll.mitie.EntityMentionVector;
import edu.mit.ll.mitie.StringVector;
import org.apache.stanbol.enhancer.servicesapi.ContentItem;

public interface MitieTextAnnotationService {
    void populateTextAnnotations(final EntityMentionVector entities, final StringVector tokens, final ContentItem contentItem, final MitieEnhancementEngine enhancementEngine);
}
