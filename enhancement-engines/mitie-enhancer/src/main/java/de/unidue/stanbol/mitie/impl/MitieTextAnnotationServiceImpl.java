package de.unidue.stanbol.mitie.impl;

import de.unidue.stanbol.mitie.MitieTextAnnotationService;
import de.unidue.stanbol.mitie.misc.MitieNerTypeToDcType;
import edu.mit.ll.mitie.EntityMention;
import edu.mit.ll.mitie.EntityMentionVector;
import edu.mit.ll.mitie.StringVector;
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
import org.apache.stanbol.enhancer.servicesapi.helper.EnhancementEngineHelper;
import org.osgi.framework.Constants;

import static org.apache.stanbol.enhancer.servicesapi.rdf.Properties.*;

@Component(immediate = true)
@Service(value = MitieTextAnnotationService.class)
@Properties(value = {
        @Property(name = Constants.SERVICE_RANKING, intValue = Integer.MIN_VALUE)
})
public class MitieTextAnnotationServiceImpl implements MitieTextAnnotationService {

    private final Language deLang = new Language("de");

    @Override
    public void populateTextAnnotations(EntityMentionVector entities, StringVector tokens, ContentItem contentItem, MitieEnhancementEngine enhancementEngine) {
        MGraph graph = contentItem.getMetadata();
        LiteralFactory literalFactory = LiteralFactory.getInstance();
        contentItem.getLock().writeLock().lock();
        try {
            for (int i = 0; i < entities.size(); i++) {
                final EntityMention entity = entities.get(i);
                final int start = entity.getStart();
                final int end = entity.getEnd();
                final String name = getTokensInRange(tokens, start, end);
                final String tag = enhancementEngine.getPossibleTags().get(entity.getTag());
                final double confidence = entity.getScore();

                final UriRef textAnnotation = EnhancementEngineHelper.createTextEnhancement(contentItem, enhancementEngine);
                final UriRef dcType = new MitieNerTypeToDcType().apply(tag);

                graph.add(new TripleImpl(textAnnotation, ENHANCER_SELECTED_TEXT, new PlainLiteralImpl(name, deLang)));
                graph.add(new TripleImpl(textAnnotation, ENHANCER_START, literalFactory.createTypedLiteral(start)));
                graph.add(new TripleImpl(textAnnotation, ENHANCER_END, literalFactory.createTypedLiteral(end)));
                graph.add(new TripleImpl(textAnnotation, ENHANCER_CONFIDENCE, literalFactory.createTypedLiteral(confidence)));
                graph.add(new TripleImpl(textAnnotation, DC_TYPE, dcType));
            }
        } finally {
            contentItem.getLock().writeLock().unlock();
        }
    }

    private String getTokensInRange(StringVector tokens, int start, int end) {
        final StringBuilder wordsBuilder = new StringBuilder();
        for (int i = start; i < end; i++) {
            wordsBuilder.append(tokens.get(i)).append(" ");
        }
        return wordsBuilder.toString().trim();
    }
}
