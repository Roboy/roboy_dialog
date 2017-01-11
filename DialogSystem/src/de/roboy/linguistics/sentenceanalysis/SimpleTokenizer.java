package de.roboy.linguistics.sentenceanalysis;

import de.roboy.linguistics.Linguistics;

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
