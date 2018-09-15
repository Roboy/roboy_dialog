package roboy.linguistics.sentenceanalysis;

import roboy.linguistics.Linguistics.SentenceType;

import java.util.List;

/**
 * Checks the sentence type by stupidly looking at the first word of the sentence
 * and hoping that there is a known question word. Puts the answer in the sentenceType
 * variable of the Interpretation object.
 */
public class DictionaryBasedSentenceTypeDetector implements Analyzer{

	@Override
	public Interpretation analyze(Interpretation interpretation) {
		List<String> tokens = interpretation.getTokens();
		String[] posTags = interpretation.getPosTags();

		//Sentence Types shall be handled in Semantic Parser Analyzer. This is a fallback, in the event that SPA does not detect the sentence type
		if(interpretation.getSentenceType().equals(SentenceType.STATEMENT)){
		    SentenceType sentenceType = determineSentenceType(tokens, posTags);
            interpretation.setSentenceType(sentenceType);

        }
		return interpretation;
	}

    /**
     * Fallback Sentence Type Detection, main Detector now in {@link SemanticParserAnalyzer}
     */
	private SentenceType determineSentenceType(List<String> tokens, String[] posTags){
		if (tokens != null && !tokens.isEmpty()) {
            String first = tokens.get(0).toLowerCase();
            switch (tokens.get(0).toLowerCase()) {
                case "who":
                    return SentenceType.WHO;
                case "what":
                    return SentenceType.WHAT;
                case "where":
                    return SentenceType.WHERE;
                case "when":
                    return SentenceType.WHEN;
                case "why":
                    return SentenceType.WHY;
                case "do":
                    return SentenceType.DOES_IT;
                case "does":
                    return SentenceType.DOES_IT;
                case "did":
                    return SentenceType.DOES_IT;
                case "is":
                    return SentenceType.IS_IT;
                case "are":
                    return SentenceType.IS_IT;
                case "am":
                    return SentenceType.IS_IT;
                case "how":
                    if (tokens.size() > 1) {
                        String second = tokens.get(1).toLowerCase();
                        if ("is".equals(second) || "are".equals(second) || "am".equals(second)) {
                            return SentenceType.HOW_IS;
                        } else if ("how".equals(first) &&
                                ("do".equals(second) || "does".equals(second) || "did".equals(second))) {
                            return SentenceType.HOW_DO;
                        } else {
                            break;
                        }
                    } else {
                        return SentenceType.HOW;
                    }
                default:
                     break;
            }

            return SentenceType.STATEMENT;
        } else {
            return SentenceType.NONE;
        }
	}
}
