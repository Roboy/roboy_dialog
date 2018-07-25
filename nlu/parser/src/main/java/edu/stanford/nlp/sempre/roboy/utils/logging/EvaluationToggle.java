package edu.stanford.nlp.sempre.roboy.utils.logging;

import fig.basic.Evaluation;

public class EvaluationToggle extends Evaluation{

    @Override
    public void logStats(String prefix) {
        if(ParserLogController.isALL())super.logStats(prefix);
    }

}