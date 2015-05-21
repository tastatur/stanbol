package de.unidue.stanford;

import edu.stanford.nlp.ling.CoreLabel;

import java.util.List;

public interface NameOccurrenceUtility {
    /**
     * Diese Methode fasst die Tokens, die zur gleichen Entität gehören, zusammen.
     * Siehe PlainTextDocumentReaderAndWriter.printAnswersInlineXML für weitere Details
     * @param labeledTokens Tokens, labeled by classificator
     * @return merged tokens
     */
    List<CoreLabel> mergeTokens(List<List<CoreLabel>> labeledTokens);
}
