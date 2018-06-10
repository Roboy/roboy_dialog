package roboy.linguistics.sentenceanalysis;

import roboy.linguistics.Linguistics;

import java.util.Arrays;
import java.util.List;

/**
 * Tokenizes the text by splitting at whitespace and stores the resulting tokens in the
 * Linguistics.TOKENS attribute of the interpretation.
 */
public class SimpleTokenizer implements Analyzer{

	@Override
	public Interpretation analyze(Interpretation interpretation) {
		String sentence = interpretation.getSentence();
		interpretation.setTokens(tokenize(sentence));
		return interpretation;
	}

	private List<String> tokenize(String sentence) {
	    if (sentence != null) {
            return Arrays.asList(sentence.toLowerCase().split("\\s+"));
        }
        return null;
	}
}
