package roboy.linguistics;

import java.util.List;

import roboy.util.Lists;

/**
 * Collection of attribute names, enumerations, word lists etc. related to linguistics.
 * 
 */
public class Linguistics {

	public enum SentenceType {
		GREETING,
		FAREWELL,
		SEGUE,
		ANECDOTE,
        STATEMENT,
		NONE,
		WHO, HOW, HOW_IS, HOW_DO, WHY, WHEN, WHERE, WHAT, IS_IT, DOES_IT
    }

	public enum SemanticRole {
		PREDICATE, AGENT, PATIENT, TIME, LOCATION, MANNER, INSTRUMENT, ORIGIN, DESTINATION,
		RECIPIENT, BENEFICIARY, PURPOSE, CAUSE
	}

    public enum ParsingOutcome {
        SUCCESS, FAILURE, UNDERSPECIFIED
    }

    public enum UtteranceSentiment {
	    POSITIVE, NEUTRAL, NEGATIVE, UNCERTAIN_POS, UNCERTAIN_NEG, MAYBE
    }

	public static final List<String> tobe = Lists.stringList("am","are","is","was","were","been");
	public static final List<String> beMod = Lists.stringList("am","are","is","was","were","has been","have been","had been");
}
