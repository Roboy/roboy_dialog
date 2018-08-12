package edu.stanford.nlp.sempre.roboy.utils.logging;

import fig.basic.*; import edu.stanford.nlp.sempre.roboy.utils.logging.*;
import org.apache.logging.log4j.Level;

public class StopwatchSetToggle extends StopWatchSet {
    public static synchronized void logStats() {
        if(ParserLogController.getLevel().isLessSpecificThan(Level.DEBUG)) StopWatchSet.logStats();
    }
}