package roboy.linguistics.sentenceanalysis;

/**
 * All linguistic analyses implement the Analyzer interface. An analyzer always takes an
 * existing interpretation of a sentence and returns one including its own analysis results 
 * (usually an enriched version of the input interpretation). 
 */
public interface Analyzer {

	public Interpretation analyze(Interpretation sentence);
	
}
