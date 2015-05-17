package de.unidue.stanford.impl;

import de.unidue.stanford.TextAnnotationService;
import edu.stanford.nlp.ling.CoreLabel;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.stanbol.enhancer.servicesapi.ContentItem;
import org.osgi.framework.Constants;

import java.util.List;

@Component(immediate = true)
@Service(value = TextAnnotationService.class)
@Properties(value = {
        @Property(name = Constants.SERVICE_RANKING, intValue = Integer.MIN_VALUE)
})
public class TextAnnotationServiceImpl implements TextAnnotationService {
    @Override
    public void populateTextAnnotations(List<List<CoreLabel>> extractedEntities, ContentItem ci) {
    }
}
