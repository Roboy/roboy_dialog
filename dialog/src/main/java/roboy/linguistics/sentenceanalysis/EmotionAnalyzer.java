package roboy.linguistics.sentenceanalysis;

import roboy.emotions.RoboyEmotion;

import java.util.List;

/**
 * Checks for a handfull of keywords and stores more or less fitting emotions
 * in the Linguistics.EMOTION feature that is later read out and fed to the
 * facial expression output module.
 */
public class EmotionAnalyzer implements Analyzer {

    public Interpretation analyze(Interpretation interpretation)
    {
        List<String> tokens = interpretation.getTokens();
        if (tokens != null && !tokens.isEmpty()) {
            if (tokens.contains("love") || tokens.contains("cute")) {
                interpretation.setEmotion(RoboyEmotion.SHY);
            } else if (tokens.contains("munich") || tokens.contains("robotics")) {
                interpretation.setEmotion(RoboyEmotion.SMILE_BLINK);
            } else if (tokens.contains("left")) {
                interpretation.setEmotion(RoboyEmotion.LOOK_LEFT);
            } else if (tokens.contains("right")) {
                interpretation.setEmotion(RoboyEmotion.LOOK_RIGHT);
            } else if (tokens.contains("cat") || tokens.contains("cats")) {
                interpretation.setEmotion(RoboyEmotion.CAT_EYES);
            } else {
                interpretation.setEmotion(RoboyEmotion.NEUTRAL);
            }

            if (interpretation.isRoboy()) {
                interpretation.setEmotion(RoboyEmotion.SMILE_BLINK);
            }
        }
        return interpretation;
    }
}
