package de.unidue.stanbol.mitie.impl;

import edu.mit.ll.mitie.NamedEntityExtractor;
import org.apache.felix.scr.annotations.*;
import org.apache.stanbol.enhancer.servicesapi.ContentItem;
import org.apache.stanbol.enhancer.servicesapi.EngineException;
import org.apache.stanbol.enhancer.servicesapi.EnhancementEngine;
import org.apache.stanbol.enhancer.servicesapi.helper.EnhancementEngineHelper;
import org.apache.stanbol.enhancer.servicesapi.impl.AbstractEnhancementEngine;
import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.component.ComponentContext;

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

    @Activate
    @Override
    protected void activate(ComponentContext ce) throws ConfigurationException {
        ner = new NamedEntityExtractor(ce.getProperties().get(MODEL_FILE_PROP).toString());
    }

    @Override
    public int canEnhance(ContentItem contentItem) throws EngineException {
        final String lang = EnhancementEngineHelper.getLanguage(contentItem);
        if ("de".equalsIgnoreCase(lang)) {
            return ENHANCE_SYNCHRONOUS;
        }
        return CANNOT_ENHANCE;
    }

    @Override
    public void computeEnhancements(ContentItem contentItem) throws EngineException {
    }
}
