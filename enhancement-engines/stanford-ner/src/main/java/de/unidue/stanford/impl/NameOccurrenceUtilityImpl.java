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
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component(immediate = true)
@Service(value = NameOccurrenceUtility.class)
@Properties(value = {
        @Property(name = Constants.SERVICE_RANKING, intValue = Integer.MIN_VALUE)
})
public class NameOccurrenceUtilityImpl implements NameOccurrenceUtility {
    @Override
    public List<CoreLabel> mergeTokens(List<List<CoreLabel>> labeledTokens) {
        List<CoreLabel> mergedTokens = new ArrayList<>();
        labeledTokens.stream().forEachOrdered(tokensInSentence -> {
            setSentenceText(tokensInSentence);
            List<CoreLabel> newTokens = mergedTokensInSentence(tokensInSentence);
            mergedTokens.addAll(newTokens);
        });
        return mergedTokens;
    }

    private List<CoreLabel> mergedTokensInSentence(List<CoreLabel> tokensInSentence) {
        List<CoreLabel> mergedTokens = new ArrayList<>();
        mergedTokens.add(tokensInSentence.get(0));
        for (int i = 1; i < tokensInSentence.size(); i++) {
            CoreLabel lastToken = mergedTokens.get(mergedTokens.size()-1);
            CoreLabel currentToken = tokensInSentence.get(i);

            String previousText = StringUtils.getNotNullString(lastToken.get(CoreAnnotations.TextAnnotation.class));
            String currentText = StringUtils.getNotNullString(currentToken.get(CoreAnnotations.TextAnnotation.class));
            String before = StringUtils.getNotNullString(currentToken.get(CoreAnnotations.BeforeAnnotation.class));
            Integer newEnd = currentToken.get(CoreAnnotations.CharacterOffsetEndAnnotation.class);

            if (shouldMerge(lastToken, currentToken)) {
                String newText = previousText.concat(before).concat(currentText);
                lastToken.set(CoreAnnotations.TextAnnotation.class, newText);
                lastToken.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, newEnd);
            } else {
                mergedTokens.add(currentToken);
            }
        }
        return mergedTokens.stream().filter(new IsNerToken()).collect(Collectors.toList());
    }

    private boolean shouldMerge(CoreLabel lastToken, CoreLabel currentToken) {
        String previousTag = StringUtils.getNotNullString(lastToken.get(CoreAnnotations.AnswerAnnotation.class));
        String currentTag = StringUtils.getNotNullString(currentToken.get(CoreAnnotations.AnswerAnnotation.class));
        String distSimLast = StringUtils.getNotNullString(lastToken.get(CoreAnnotations.DistSimAnnotation.class));
        String distSimCurrent = StringUtils.getNotNullString(currentToken.get(CoreAnnotations.DistSimAnnotation.class));
        return currentTag.equals(previousTag) && !currentTag.equals(SeqClassifierFlags.DEFAULT_BACKGROUND_SYMBOL) &&
                !distSimLast.equals("null") && !distSimCurrent.equals("null");
    }

    public void setSentenceText(List<CoreLabel> sentenceText) {
        StringBuilder sentenceBuilder = new StringBuilder();
        sentenceText.stream().forEachOrdered(token -> {
            String tokenText = StringUtils.getNotNullString(token.get(CoreAnnotations.TextAnnotation.class));
            String before = StringUtils.getNotNullString(token.get(CoreAnnotations.BeforeAnnotation.class));
            sentenceBuilder.append(before).append(tokenText);
        });
        String text = sentenceBuilder.toString();
        sentenceText.parallelStream().forEach(token -> token.set(CoreAnnotations.CharAnnotation.class, text));
    }

    private class IsNerToken implements Predicate<CoreLabel> {
        @Override
        public boolean test(CoreLabel coreLabel) {
            final String answer = StringUtils.getNotNullString(coreLabel.get(CoreAnnotations.AnswerAnnotation.class));
            final String distSim = StringUtils.getNotNullString(coreLabel.get(CoreAnnotations.DistSimAnnotation.class));
            return !answer.equals(SeqClassifierFlags.DEFAULT_BACKGROUND_SYMBOL) && !distSim.equals("null");
        }
    }
}
