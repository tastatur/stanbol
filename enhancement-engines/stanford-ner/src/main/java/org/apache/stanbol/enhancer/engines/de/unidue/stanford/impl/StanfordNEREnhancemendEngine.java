package org.apache.stanbol.enhancer.engines.de.unidue.stanford.impl;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import org.apache.felix.scr.annotations.*;
import org.apache.stanbol.enhancer.nlp.model.AnalysedText;
import org.apache.stanbol.enhancer.nlp.model.AnalysedTextUtils;
import org.apache.stanbol.enhancer.nlp.model.Section;
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
import java.util.ArrayList;
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
        AnalysedText analysedText = AnalysedTextUtils.getAnalysedText(ci);
        if (cantWorkWithText(analysedText)) {
            log.warn("Can't enhance text");
            return;
        }

        List<CoreLabel> extractedEntities = extractEntities(analysedText);
        log.info("Stanford NER is done, hopefully without killing JVM with OOM exception.");
        log.info("Now is time for RDF creation!");

        createTextAnnotations(ci, extractedEntities);
    }

    private boolean cantWorkWithText(AnalysedText analysedText) {
        return (analysedText == null || !analysedText.getTokens().hasNext());
    }

    private List<CoreLabel> extractEntities(AnalysedText analysedText) {
        List<CoreLabel> coreLabels = new ArrayList<>();
        List<Section> sentences = new ArrayList<>();
        AnalysedTextUtils.appandToList(analysedText.getSentences(), sentences);
        if (sentences.isEmpty()) {
            sentences.add(analysedText);
        }

        for (Section sentence : sentences) {
            coreLabels.addAll(extractEntitiesFromSentence(sentence));
        }
        return coreLabels;
    }

    private List<CoreLabel> extractEntitiesFromSentence(Section sentence) {
        List<CoreLabel> coreLabels = new ArrayList<>();
        sentence.getTokens().forEachRemaining(token -> {
            CoreLabel label = new CoreLabel();
            label.setWord(token.getSpan());
            label.setValue(token.getSpan());
            label.setOriginalText(sentence.getSpan());
            coreLabels.add(label);
        });

        return classifier.classifySentence(coreLabels);
    }

    private void createTextAnnotations(ContentItem ci, List<CoreLabel> extractedEntities) {
        ci.getLock().writeLock().lock();
        try {
            log.info("Test");
        } finally {
            ci.getLock().writeLock().unlock();
        }
    }
}
