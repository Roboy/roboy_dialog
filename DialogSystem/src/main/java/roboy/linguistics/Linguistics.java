package roboy.linguistics;

import java.util.Arrays;
import java.util.List;

import roboy.util.Lists;

/**
 * Collection of attribute names, enumerations, word lists etc. related to linguistics.
 */
public class Linguistics {

	public enum SENTENCE_TYPE { 
		GREETING,
		FAREWELL,
		SEGUE,
		ANECDOTE,
		WHO, HOW_IS, HOW_DO, WHY, WHEN, WHERE, WHAT, IS_IT, DOES_IT, STATEMENT, NONE
		}
	
	public enum SEMANTIC_ROLE {
		PREDICATE, AGENT, PATIENT, TIME, LOCATION, MANNER, INSTRUMENT, ORIGIN, DESTINATION,
		RECIPIENT, BENEFICIARY, PURPOSE, CAUSE
	}
	
	public static final List<String> tobe = Arrays.asList("am","are","is","was","were","been");
	public static final List<String> beMod = Lists.stringList("am","are","is","was","were","has been","have been","had been");
	
	// feature names
	public static final String SENTENCE = "sentence";
	public static final String TRIPLE = "triple";
	public static final String TOKENS = "tokens";
	public static final String POSTAGS = "postags";
	public static final String KEYWORDS = "keywords";
	public static final String ASSOCIATION = "association";
	public static final String PAS = "pas";
	public static final String NAME = "name";
	public static final String CELEBRITY = "celebrity";
	public static final String ROBOYDETECTED = "roboydetected";
	public static final String OBJ_ANSWER = "objanswer";
	public static final String PRED_ANSWER = "predanswer";
	
}
