package de.unidue.stanford.impl;

import de.unidue.stanford.NameOccurrenceUtility;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.sequences.SeqClassifierFlags;
import edu.stanford.nlp.util.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.Constants;

import java.util.ArrayList;
import java.util.List;

@Component(immediate = true)
@Service(value = NameOccurrenceUtility.class)
@Properties(value = {
        @Property(name = Constants.SERVICE_RANKING, intValue = Integer.MIN_VALUE)
})
public class NameOccurrenceUtilityImpl implements NameOccurrenceUtility {
    @Override
    public List<List<CoreLabel>> mergeTokens(List<List<CoreLabel>> labeledTokens) {
        List<List<CoreLabel>> mergedTokens = new ArrayList<>();
        labeledTokens.stream().forEachOrdered(tokensInSentence -> {
            List<CoreLabel> newTokens = mergedTokensInSentence(tokensInSentence);
            mergedTokens.add(newTokens);
        });
        return mergedTokens;
    }

    private List<CoreLabel> mergedTokensInSentence(List<CoreLabel> tokensInSentence) {
        List<CoreLabel> mergedTokens = new ArrayList<>();
        mergedTokens.add(tokensInSentence.get(0));
        for (int i = 1; i < tokensInSentence.size(); i++) {
            CoreLabel lastToken = mergedTokens.get(mergedTokens.size()-1);
            CoreLabel currentToken = tokensInSentence.get(i);

            String previousTag = StringUtils.getNotNullString(lastToken.get(CoreAnnotations.AnswerAnnotation.class));
            String currentTag = StringUtils.getNotNullString(currentToken.get(CoreAnnotations.AnswerAnnotation.class));
            String previousText = StringUtils.getNotNullString(lastToken.get(CoreAnnotations.OriginalTextAnnotation.class));
            String currentText = StringUtils.getNotNullString(currentToken.get(CoreAnnotations.OriginalTextAnnotation.class));
            String before = StringUtils.getNotNullString(currentToken.get(CoreAnnotations.BeforeAnnotation.class));
            Integer newEnd = currentToken.get(CoreAnnotations.CharacterOffsetEndAnnotation.class);

            if (shouldMerge(currentTag, previousTag)) {
                String newText = previousText.concat(before).concat(currentText);
                lastToken.set(CoreAnnotations.OriginalTextAnnotation.class, newText);
                lastToken.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, newEnd);
            } else {
                mergedTokens.add(currentToken);
            }
        }
        return mergedTokens;
    }

    private boolean shouldMerge(String currentTag, String previousTag) {
        return currentTag.equals(previousTag) && !currentTag.equals(SeqClassifierFlags.DEFAULT_BACKGROUND_SYMBOL);
    }
}
