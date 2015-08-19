package de.unidue.stanbol.stanford.impl;

import de.unidue.stanbol.stanford.NameOccurrenceUtility;
import de.unidue.stanbol.stanford.StanfordEnhancemendMode;
import de.unidue.stanbol.stanford.StanfordTextAnnotationService;
import edu.stanford.nlp.ie.NERClassifierCombiner;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import org.apache.felix.scr.annotations.*;
import org.apache.stanbol.enhancer.nlp.model.AnalysedText;
import org.apache.stanbol.enhancer.nlp.model.AnalysedTextFactory;
import org.apache.stanbol.enhancer.nlp.utils.NlpEngineHelper;
import org.apache.stanbol.enhancer.servicesapi.ContentItem;
import org.apache.stanbol.enhancer.servicesapi.EngineException;
import org.apache.stanbol.enhancer.servicesapi.EnhancementEngine;
import org.apache.stanbol.enhancer.servicesapi.helper.EnhancementEngineHelper;
import org.apache.stanbol.enhancer.servicesapi.impl.AbstractEnhancementEngine;
import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@Component(
        metatype = true,
        immediate = true,
        inherit = true,
        configurationFactory = true,
        policy = ConfigurationPolicy.REQUIRE)
@Service
@org.apache.felix.scr.annotations.Properties(value = {
        @Property(name = EnhancementEngine.PROPERTY_NAME, value = "stanford"),
        @Property(name = StanfordNEREnhancemendEngine.ENHANCEMENT_MODE, options = {
                @PropertyOption(
                        value = "DEWAC",
                        name = "DEWAC"),
                @PropertyOption(
                        value = "HGC",
                        name = "HGC"),
                @PropertyOption(
                        value = "BOTH",
                        name = "BOTH")
        }, value = "BOTH"),
        @Property(name = Constants.SERVICE_RANKING, intValue = -100)
})
public class StanfordNEREnhancemendEngine extends AbstractEnhancementEngine<RuntimeException, RuntimeException> implements EnhancementEngine {
    public static final String ENHANCEMENT_MODE = "enhancer.engines.stanford.mode";
    private final Logger log = LoggerFactory.getLogger(getClass());

    private NERClassifierCombiner combinedClassifier;
    private CRFClassifier<CoreLabel> singleClassifier;

    @SuppressWarnings("all")
    @Reference
    private AnalysedTextFactory analysedTextFactory;

    @SuppressWarnings("all")
    @Reference
    private StanfordTextAnnotationService stanfordTextAnnotationService;

    @SuppressWarnings("all")
    @Reference
    private NameOccurrenceUtility nameOccurrenceUtility;

    @Activate
    @Override
    protected void activate(ComponentContext ce) throws ConfigurationException {
        super.activate(ce);

        final String enhancementMode = (String) ce.getProperties().get(ENHANCEMENT_MODE);
        switch (StanfordEnhancemendMode.valueOf(enhancementMode)) {
            case DEWAC:
                @SuppressWarnings("all")
                String dewacClassifier = getClass().getClassLoader().getResource("classifiers/edu/stanford/nlp/models/ner/german.dewac_175m_600.crf.ser.gz").getPath();
                loadSingleClassifier(dewacClassifier);
                break;
            case HGC:
                @SuppressWarnings("all")
                String hgcClassifier = getClass().getClassLoader().getResource("classifiers/edu/stanford/nlp/models/ner/german.hgc_175m_600.crf.ser.gz").getPath();
                loadSingleClassifier(hgcClassifier);
                break;
            default:
                mixBothClassifiers();
        }
        log.info("Stanford NER loaded");
    }

    private void loadSingleClassifier(final String classifierPath) {
        try {
            singleClassifier = CRFClassifier.getClassifier(classifierPath);
        } catch (IOException | ClassNotFoundException e) {
            log.error("Can't activate stanford NER!");
            throw new RuntimeException(e);
        }
    }

    private void mixBothClassifiers() {
        @SuppressWarnings("all")
        String dewacClassifier = getClass().getClassLoader().getResource("classifiers/edu/stanford/nlp/models/ner/german.dewac_175m_600.crf.ser.gz").getPath();
        @SuppressWarnings("all")
        String hgcClassifier = getClass().getClassLoader().getResource("classifiers/edu/stanford/nlp/models/ner/german.hgc_175m_600.crf.ser.gz").getPath();
        try {
            java.util.Properties nerProps = new java.util.Properties();
            nerProps.setProperty("ner.model", dewacClassifier.concat(",").concat(hgcClassifier));
            combinedClassifier = NERClassifierCombiner.createNERClassifierCombiner(null, nerProps);
        } catch (Exception e) {
            log.error("Can't activate stanford NER!");
            throw new RuntimeException(e);
        }
    }

    @Deactivate
    @Override
    protected void deactivate(ComponentContext ce) {
        super.deactivate(ce);
        combinedClassifier = null;
        singleClassifier = null;
        log.info("Stanford NER unloaded");
    }

    @Override
    public int canEnhance(ContentItem ci) throws EngineException {
        String lang = EnhancementEngineHelper.getLanguage(ci);
        if ("de".equalsIgnoreCase(lang)) {
            return ENHANCE_SYNCHRONOUS;
        }
        return CANNOT_ENHANCE;
    }

    @Override
    public void computeEnhancements(ContentItem ci) throws EngineException {
        AnalysedText analysedText = NlpEngineHelper.initAnalysedText(this, analysedTextFactory, ci);
        List<List<CoreLabel>> labeledTokens;
        if (combinedClassifier != null) {
            labeledTokens = combinedClassifier.classify(analysedText.getText().toString());
        } else {
            labeledTokens = singleClassifier.classify(analysedText.getText().toString());
        }
        List<CoreLabel> extractedEntities = nameOccurrenceUtility.mergeTokens(labeledTokens);
        stanfordTextAnnotationService.populateTextAnnotations(extractedEntities, ci, this);
    }
}
