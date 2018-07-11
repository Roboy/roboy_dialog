package roboy.linguistics.sentenceanalysis;

import roboy.linguistics.Linguistics;

import java.util.Arrays;
import java.util.List;

/**
 * Tokenizes the text by splitting at whitespace and stores the resulting tokens in the
 * Linguistics.TOKENS attribute of the interpretation.
 *
 * This class was deprecated because the tokens are anyways overwritten by SemanticParserAnalyzer. SmeanticParserAnalyzer is run next and does not require the analysis of SimpleTokenizer to function. Furthermore ST's approach to Parsing is not exactly correct.
 *
 * [Roboy]: Do I need a visa to go to suzhou?
 * [You]:   Yes Roboy, you do
 *
 * Simple Tokenizer:
 *     0 = "yes"
 *     1 = "roboy,"
 *     2 = "you"
 *     3 = "do"
 *
 * Semantic Parser Analyzer: (Desired)
 *     0 = "yes"
 *     1 = "roboy"
 *     2 = ","
 *     3 = "you"
 *     4 = "do"
 *
 */
@Deprecated
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
