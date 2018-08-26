package org.roboy.linguistics.sentenceanalysis;

/**
 * Corrects abbreviated forms like "I'm" to complete forms like "I am"
 * which are expected by later sentence analyses.
 */
public class Postprocessor implements Analyzer{
    public Interpretation analyze(Interpretation interpretation){
        interpretation.toLowerCase();
        return interpretation;
    }
}
