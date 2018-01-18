package roboy.linguistics.sentenceanalysis;

import roboy.linguistics.Linguistics;

/**
 * Tokenizes the text by splitting at whitespace and stores the resulting tokens in the
 * Linguistics.TOKENS attribute of the interpretation.
 */
public class SimpleTokenizer implements Analyzer{

	@Override
	public Interpretation analyze(Interpretation interpretation) {
		String sentence = (String) interpretation.getFeatures().get(Linguistics.SENTENCE);
		String[] tokens = tokenize(sentence);
		interpretation.getFeatures().put(Linguistics.TOKENS,tokens);
		return interpretation;
	}

	private String[] tokenize(String sentence){
		return sentence.split("\\s+");
	}
}
