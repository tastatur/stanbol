package de.unidue.stanbol.mitie.impl;

import de.unidue.stanbol.mitie.MitieTextAnnotationService;
import edu.mit.ll.mitie.EntityMentionVector;
import edu.mit.ll.mitie.NamedEntityExtractor;
import edu.mit.ll.mitie.StringVector;
import org.apache.felix.scr.annotations.*;
import org.apache.stanbol.enhancer.nlp.model.AnalysedText;
import org.apache.stanbol.enhancer.nlp.model.AnalysedTextUtils;
import org.apache.stanbol.enhancer.servicesapi.ContentItem;
import org.apache.stanbol.enhancer.servicesapi.EngineException;
import org.apache.stanbol.enhancer.servicesapi.EnhancementEngine;
import org.apache.stanbol.enhancer.servicesapi.helper.EnhancementEngineHelper;
import org.apache.stanbol.enhancer.servicesapi.impl.AbstractEnhancementEngine;
import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.component.ComponentContext;

import java.nio.file.Paths;

@Component(
        metatype = true,
        immediate = true,
        inherit = true,
        configurationFactory = true,
        policy = ConfigurationPolicy.REQUIRE)
@Service
@org.apache.felix.scr.annotations.Properties(value = {
        @Property(name = EnhancementEngine.PROPERTY_NAME, value = "mitie"),
        @Property(name = MitieEnhancementEngine.MODEL_FILE_PROP, value = "tiger-mt-model.dat"),
        @Property(name = Constants.SERVICE_RANKING, intValue = -100)
})
public class MitieEnhancementEngine extends AbstractEnhancementEngine<RuntimeException, RuntimeException> implements EnhancementEngine {

    public static final String MODEL_FILE_PROP = "enhancer.engines.mitie.model";

    private NamedEntityExtractor ner;

    @SuppressWarnings("all")
    @Reference
    private MitieTextAnnotationService mitieTextAnnotationService;

    @Activate
    @Override
    protected void activate(ComponentContext ce) throws ConfigurationException {
        final String modelFile = ce.getProperties().get(MODEL_FILE_PROP).toString();
        final String modelsDataDir = ce.getBundleContext().getProperty("de.unidue.modelsdir");
        final String modelPath = Paths.get(modelsDataDir.concat(modelFile)).toAbsolutePath().toString();
        ner = new NamedEntityExtractor(modelPath);
    }

    @Override
    public int canEnhance(ContentItem contentItem) throws EngineException {
        final String lang = EnhancementEngineHelper.getLanguage(contentItem);
        final AnalysedText at = AnalysedTextUtils.getAnalysedText(contentItem);
        if ("de".equalsIgnoreCase(lang) && at != null && at.getTokens().hasNext()) {
            return ENHANCE_SYNCHRONOUS;
        }
        return CANNOT_ENHANCE;
    }

    @Override
    public void computeEnhancements(ContentItem contentItem) throws EngineException {
        final AnalysedText at = AnalysedTextUtils.getAnalysedText(contentItem);
        final StringVector tokens = new StringVector();
        at.getTokens().forEachRemaining(token -> tokens.add(token.getSpan()));
        final EntityMentionVector entities = ner.extractEntities(tokens);
        mitieTextAnnotationService.populateTextAnnotations(entities, tokens, contentItem, this);
    }

    public StringVector getPossibleTags() {
        return ner.getPossibleNerTags();
    }
}
