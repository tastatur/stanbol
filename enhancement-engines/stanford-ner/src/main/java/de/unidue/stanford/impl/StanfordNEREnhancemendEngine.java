package de.unidue.stanford.impl;

import de.unidue.stanford.NameOccurenceUtility;
import de.unidue.stanford.TextAnnotationService;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
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

@Component(immediate = true, metatype = true)
@Service
@Properties(value = {
        @Property(name = EnhancementEngine.PROPERTY_NAME, value = "stanford-ner"),
        @Property(name = Constants.SERVICE_RANKING, intValue = -100)
}
)
public class StanfordNEREnhancemendEngine extends AbstractEnhancementEngine<RuntimeException, RuntimeException> implements EnhancementEngine {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private AbstractSequenceClassifier<CoreLabel> classifier;

    @SuppressWarnings("all")
    @Reference
    private AnalysedTextFactory analysedTextFactory;

    @SuppressWarnings("all")
    @Reference
    private TextAnnotationService textAnnotationService;

    @SuppressWarnings("all")
    @Reference
    private NameOccurenceUtility nameOccurenceUtility;

    @Activate
    @Override
    protected void activate(ComponentContext ce) throws ConfigurationException {
        super.activate(ce);

        @SuppressWarnings("all")
        String classifierPath = getClass().getClassLoader().getResource("classifiers/edu/stanford/nlp/models/ner/german.dewac_175m_600.crf.ser.gz").getPath();
        try {
            classifier = CRFClassifier.getClassifier(classifierPath);
        } catch (IOException | ClassNotFoundException e) {
            log.error("Can't activate stanford NER!");
            throw new RuntimeException(e);
        }
        log.info("Stanford NER loaded");
    }

    @Deactivate
    @Override
    protected void deactivate(ComponentContext ce) {
        super.deactivate(ce);
        classifier = null;
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
        List<List<CoreLabel>> labeledTokens = classifier.classify(analysedText.getText().toString());
        List<List<CoreLabel>> extractedEntities = nameOccurenceUtility.mergeTokens(labeledTokens);
        textAnnotationService.populateTextAnnotations(extractedEntities, ci);
    }
}
