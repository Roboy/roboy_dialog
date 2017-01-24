package roboy.linguistics;

public class Linguistics {

	public enum SENTENCE_TYPE { 
		GREETING,
		FAREWELL,
		SEGUE,
		ANECDOTE,
		WHO, HOW_IS, WHY, WHEN, WHERE, WHAT, IS_IT, DOES_IT, STATEMENT, NONE
		}
	
	public static final String SENTENCE = "sentence";
	public static final String TRIPLE = "triple";
	public static final String TOKENS = "tokens";
	public static final String POSTAGS = "postags";
	public static final String KEYWORDS = "keywords";
	public static final String ASSOCIATION = "association";
}
