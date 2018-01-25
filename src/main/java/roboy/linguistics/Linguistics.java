package roboy.linguistics;

import java.util.Arrays;
import java.util.List;

import roboy.util.Lists;

/**
 * Collection of attribute names, enumerations, word lists etc. related to linguistics.
 * 
 * Most importantly it contains the names of the results of the Analyzer that are stored in
 * an Interpretation object and can be retrieved by the getFeature(String featureName) method.
 * These feature names include:
 * SENTENCE
 * TRIPLE
 * TOKENS
 * POSTAGS
 * KEYWORDS
 * ASSOCIATION
 * PAS
 * NAME
 * CELEBRITY
 * OBJ_ANSWER
 * PRED_ANSWER
 * EMOTION
 * INTENT
 * INTENT_DISTANCE
 * 
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
	
	/**
	 * The utterance of the person Roboy is speaking to
	 */
	public static final String SENTENCE = "sentence";

	/**
	 * A triple of subject, predicate and object extracted by a very primitive rule system
	 */
	public static final String TRIPLE = "triple";
	
	/**
	 * The tokens (usually words) of the sentence
	 */
	public static final String TOKENS = "tokens";
	
	/**
	 * The part-of-speech tags (noun, verb, adjective etc.) corresponding to the tokens
	 */
	public static final String POSTAGS = "postags";
	
	/**
	 * If keywords for the segue state from the resource knowledgebase/triviaWords.csv are detected, they are passed with this name
	 */
	public static final String KEYWORDS = "keywords";
	
	/**
	 * Is used to pass the detected keyword from the segue state to the verbalizer state to mention it before telling the anecdote
	 */
	public static final String ASSOCIATION = "association";
	
	/**
	 * Predicate-argument structures (who(agens) did what(predicate) to whom(patiens))
	 */
	public static final String PAS = "pas";
	
	/**
	 * Internally used to retrieve the name of a concept
	 */
	public static final String NAME = "name";
	
	/**
	 * The name of the celebrity most resembling the person talked to, as detected by the CelebritySimilarityInput
	 */
	public static final String CELEBRITY = "celebrity";
	
	/**
	 * If Roboy detected his own name
	 */
	public static final String ROBOYDETECTED = "roboydetected";
	
	/**
	 * Contains the answer to a question asked by the QuestionAskingState, if the answer is expected to be in the
	 * object of the sentence, like if the question is "What is your name?" or "Where are you from?"
	 */
	public static final String OBJ_ANSWER = "objanswer";
	
	/**
	 * Contains the answer to a question asked by the QuestionAskingState, if the answer is expected to be a predicate
	 * or a predicate and an object of the sentence, like if the question is "What is your hobby?" or "What do you do
	 * for a living?"
	 */
	public static final String PRED_ANSWER = "predanswer";
	
	/**
	 * Contains the emotion Roboy intends to express based on the keyword detection in the EmotionAnalyzer
	 */
	public static final String EMOTION = "emotion";
	
	/**
	 * The result of the machine learning intent classification in the IntentAnalyzer
	 */
	public static final String INTENT = "intent";
	
	/**
	 * The confidence score of the machine learning intent classification in the IntentAnalyzer
	 */
	public static final String INTENT_DISTANCE = "intentdistance";

	/**
	 * The result of SemanticParserAnalyzer, formal language representation
	 */
	public static final String PARSE = "parse";
	
}
