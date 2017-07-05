package roboy.linguistics.sentenceanalysis;

import roboy.linguistics.Linguistics;

/**
 * Created by roboy on 7/5/17.
 */
public class Preprocessor implements Analyzer{
    public Interpretation analyze(Interpretation sentence){
        String s = (String) sentence.getFeature(Linguistics.SENTENCE);
        s = s.replaceAll("i'm", "I am");
        s = s.replaceAll("'ve", " have");
        s = s.replaceAll("n't", " not");
        sentence.getFeatures().put(Linguistics.SENTENCE, s);
        return  sentence;
    }
}
