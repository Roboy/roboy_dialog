package edu.stanford.nlp.sempre.roboy.utils.logging;

import fig.basic.*;
import opennlp.tools.parser.Parse;

public class EvaluationToggler extends Evaluation{

    public void logStats(String prefix) {
        if(ParserLogController.isALL())super.logStats(prefix);
    }

}