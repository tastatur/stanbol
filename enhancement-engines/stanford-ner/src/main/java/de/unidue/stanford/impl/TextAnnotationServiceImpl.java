package de.unidue.stanford.impl;

import de.unidue.stanford.TextAnnotationService;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import org.apache.clerezza.rdf.core.Language;
import org.apache.clerezza.rdf.core.LiteralFactory;
import org.apache.clerezza.rdf.core.MGraph;
import org.apache.clerezza.rdf.core.UriRef;
import org.apache.clerezza.rdf.core.impl.PlainLiteralImpl;
import org.apache.clerezza.rdf.core.impl.TripleImpl;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.stanbol.enhancer.servicesapi.ContentItem;
import org.apache.stanbol.enhancer.servicesapi.EnhancementEngine;
import org.apache.stanbol.enhancer.servicesapi.helper.EnhancementEngineHelper;
import org.osgi.framework.Constants;

import java.util.List;

import static org.apache.stanbol.enhancer.servicesapi.rdf.Properties.*;

@Component(immediate = true)
@Service(value = TextAnnotationService.class)
@Properties(value = {
        @Property(name = Constants.SERVICE_RANKING, intValue = Integer.MIN_VALUE)
})
public class TextAnnotationServiceImpl implements TextAnnotationService {
    private final Language deLang = new Language("de");

    @Override
    public void populateTextAnnotations(List<CoreLabel> extractedEntities, ContentItem ci, EnhancementEngine engine) {
        MGraph graph = ci.getMetadata();
        LiteralFactory literalFactory = LiteralFactory.getInstance();
        ci.getLock().writeLock().lock();
        try {
            extractedEntities.forEach(ner -> {
                String name = ner.get(CoreAnnotations.OriginalTextAnnotation.class);
                String context = ner.get(CoreAnnotations.CharAnnotation.class);
                Integer start = ner.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class);
                Integer end = ner.get(CoreAnnotations.CharacterOffsetEndAnnotation.class);

                UriRef textAnnotation = EnhancementEngineHelper.createTextEnhancement(ci, engine);

                graph.add(new TripleImpl(textAnnotation, ENHANCER_SELECTED_TEXT, new PlainLiteralImpl(name, deLang)));
                graph.add(new TripleImpl(textAnnotation, ENHANCER_SELECTION_CONTEXT, new PlainLiteralImpl(context, deLang)));
                graph.add(new TripleImpl(textAnnotation, ENHANCER_START, literalFactory.createTypedLiteral(start)));
                graph.add(new TripleImpl(textAnnotation, ENHANCER_END, literalFactory.createTypedLiteral(end)));
            });
        } finally {
            ci.getLock().writeLock().unlock();
        }
    }
}
