package org.apache.stanbol.enhancer.engines.de.unidue.stanford.impl;

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
@SuppressWarnings("all")
public class StanfordNEREnhancemendEngine extends AbstractEnhancementEngine<RuntimeException, RuntimeException> implements EnhancementEngine {
    private final Logger log = LoggerFactory.getLogger(getClass());
    public static final AbstractSequenceClassifier<CoreLabel> CLASSIFIER;

    @Reference
    private AnalysedTextFactory analysedTextFactory;

    //Ja, ich weiß, das ist schmutzig, aber die Lösung ist halt die schnellste :)
    static {
        String classifierPath = StanfordNEREnhancemendEngine.class.getClassLoader().getResource("classifiers/edu/stanford/nlp/models/ner/german.dewac_175m_600.crf.ser.gz").getPath();
        AbstractSequenceClassifier<CoreLabel> classifier = null;
        try {
            classifier = CRFClassifier.getClassifier(classifierPath);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        CLASSIFIER = classifier;
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
        List<List<CoreLabel>> extractedEntities = CLASSIFIER.classify(analysedText.getText().toString());
        log.debug("Stanford NER is done, hopefully without killing JVM with OOM exception");
    }
}
