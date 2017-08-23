package roboy.linguistics.sentenceanalysis;

import roboy.linguistics.Linguistics;
import roboy.ros.RosMainNode;

/**
 * Created by laura on 21.08.17.
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
