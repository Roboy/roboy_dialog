package roboy.linguistics.sentenceanalysis;

import roboy.linguistics.Linguistics;
import roboy.ros.RosMainNode;

/**
 * Calls a machine learning model to determine if the utterance of the other person represents
 * one of the learned intents. Stores the highest scoring intent in the Linguistics.INTENT feature
 * and the score in the Linguistics.INTENT_DISTANCE feature.
 */
public class IntentAnalyzer implements Analyzer {
    private RosMainNode ros;

    public IntentAnalyzer(RosMainNode ros) {
        this.ros = ros;
    }

    @Override
    public Interpretation analyze(Interpretation sentence) {
        Object[] intent = (Object[]) ros.DetectIntent((String) sentence.getFeature(Linguistics.SENTENCE));
        if(intent.length == 2) {
            try {
                sentence.getFeatures().put(Linguistics.INTENT, intent[0]);
                sentence.getFeatures().put(Linguistics.INTENT_DISTANCE, intent[1]);
            } catch (RuntimeException e) {
                System.out.println("Exception while parsing intent response: " + e.getStackTrace());
            }
        }
        return sentence;
    }
}
