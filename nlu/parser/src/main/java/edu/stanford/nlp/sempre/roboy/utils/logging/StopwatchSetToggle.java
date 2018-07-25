package edu.stanford.nlp.sempre.roboy.utils.logging;

import fig.basic.*; import edu.stanford.nlp.sempre.roboy.utils.logging.*;

public class StopwatchSetToggle extends StopWatchSet {
    public static synchronized void logStats() {
        if(ParserLogController.isALL()) StopWatchSet.logStats();
    }
}