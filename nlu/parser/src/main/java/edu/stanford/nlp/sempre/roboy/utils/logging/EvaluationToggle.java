package edu.stanford.nlp.sempre.roboy.utils.logging;

import fig.basic.Evaluation;
import org.apache.logging.log4j.Level;

/**
 * Replaces all the methods found in {@link Evaluation} class with toggles, based off the Levels in {@link ParserLogController}
 */
public class EvaluationToggle extends Evaluation{

    @Override
    public void logStats(String prefix) {
        if(ParserLogController.getLevel().isLessSpecificThan(Level.DEBUG))super.logStats(prefix);
    }

}