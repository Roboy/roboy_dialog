package roboy.dialog.personality.states;

import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.util.Lists;

import java.util.List;

public class IdleState extends AbstractBooleanState {


    @Override
    public List<Interpretation> act() {
        return Lists.interpretationList(new Interpretation("I'm so lonely."));
    }


    @Override
    protected boolean determineSuccess(Interpretation input) {
        String sentence = (String) input.getFeatures().get(Linguistics.SENTENCE);
        return !sentence.isEmpty();
    }
}
