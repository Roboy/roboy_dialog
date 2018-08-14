package roboy.linguistics.sentenceanalysis;

import roboy.talk.PhraseCollection;
import roboy.util.RandomList;
import java.util.List;

/**
 * Checks for words and stores if the sentence has profanity
 * in the Interpretation Profanity feature that is later read out and fed to the
 * output module.
 */

public class ProfanityAnalyzer implements Analyzer{

    @Override
    public Interpretation analyze(Interpretation sentence) {
        
        RandomList<String> checkList = PhraseCollection.PROFANITY_CHECK_WORDS;
        List<String> tokens = sentence.getTokens();

        for (int i = 0; i < checkList.size(); i++) {
            if (tokens.contains(checkList.get(i))) {
                sentence.setProfanity(true);
                return sentence;
            }
        }
        sentence.setProfanity(false);

        return sentence;
    }
}