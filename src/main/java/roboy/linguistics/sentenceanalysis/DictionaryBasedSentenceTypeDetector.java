package roboy.linguistics.sentenceanalysis;

import roboy.linguistics.Linguistics;
import roboy.linguistics.Linguistics.SENTENCE_TYPE;

/**
 * Checks the sentence type by stupidly looking at the first word of the sentence
 * and hoping that there is a known question word. Puts the answer in the sentenceType
 * variable of the Interpretation object.
 */
public class DictionaryBasedSentenceTypeDetector implements Analyzer{

	@Override
	public Interpretation analyze(Interpretation interpretation) {
		String[] tokens = (String[]) interpretation.getFeatures().get(Linguistics.TOKENS);
		String[] posTags = (String[]) interpretation.getFeatures().get(Linguistics.POSTAGS);
		SENTENCE_TYPE sentenceType = determineSentenceType(tokens, posTags);
		interpretation.setSentenceType(sentenceType);
		return interpretation;
	}

	private SENTENCE_TYPE determineSentenceType(String[] tokens, String[] posTags){
		if(tokens.length==0) return SENTENCE_TYPE.NONE;
		String first = tokens[0].toLowerCase();
		if("who".equals(first)) return SENTENCE_TYPE.WHO;
		if("yes".equals(first)) return SENTENCE_TYPE.YES;
		if("no".equals(first)) return SENTENCE_TYPE.NO;
		if("stop".equals(first)) return SENTENCE_TYPE.STOP;
		if("where".equals(first)) return SENTENCE_TYPE.WHERE;
		if("what".equals(first)) return SENTENCE_TYPE.WHAT;
		if("when".equals(first)) return SENTENCE_TYPE.WHEN;
		if("why".equals(first)) return SENTENCE_TYPE.WHY;
		if("do".equals(first)) return SENTENCE_TYPE.DOES_IT;
		if("does".equals(first)) return SENTENCE_TYPE.DOES_IT;
		if("did".equals(first)) return SENTENCE_TYPE.DOES_IT;
		if("is".equals(first)) return SENTENCE_TYPE.IS_IT;
		if("are".equals(first)) return SENTENCE_TYPE.IS_IT;
		if("am".equals(first)) return SENTENCE_TYPE.IS_IT;
		if(tokens.length==1) return SENTENCE_TYPE.STATEMENT;
		String second = tokens[1].toLowerCase();
		if("how".equals(first) && 
				("is".equals(second)||"are".equals("second")||"am".equals(second))){
			return SENTENCE_TYPE.HOW_IS;
		}
		if("how".equals(first) && 
				("do".equals(second)||"did".equals("second"))){
			return SENTENCE_TYPE.HOW_DO;
		}
		return SENTENCE_TYPE.STATEMENT;
	}
}
