package roboy.linguistics.sentenceanalysis;

import roboy.linguistics.Linguistics;

/**
 * Corrects abbreviated forms like "I'm" to complete forms like "I am"
 * which are expected by later sentence analyses.
 */
public class Preprocessor implements Analyzer{
    public Interpretation analyze(Interpretation interpretation){
        String sentence = interpretation.getSentence();
        if (sentence != null) {
            sentence = sentence.replaceAll("i'm", "I am");
            sentence = sentence.replaceAll("'ve", " have");
            sentence = sentence.replaceAll("n't", " not");
        }
        interpretation.setSentence(sentence);
        return interpretation;
    }
}
