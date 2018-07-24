package edu.stanford.nlp.sempre.roboy.utils.logging;

import fig.basic.*;

public class StopwatchSetToggler extends StopWatchSet {
    public static synchronized void logStats() {
        if(ParserLogController.isALL()) StopWatchSet.logStats();
    }
}