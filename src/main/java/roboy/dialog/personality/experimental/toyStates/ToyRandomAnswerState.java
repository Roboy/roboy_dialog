package roboy.dialog.personality.experimental.toyStates;

import roboy.dialog.personality.experimental.AbstractState;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.util.Lists;

import java.util.List;

public class ToyRandomAnswerState extends AbstractState {

    public ToyRandomAnswerState(String stateIdentifier) {
        super(stateIdentifier);
    }

    @Override
    public List<Interpretation> act() {
        return null; // this state is only used as fallback, no act needed
    }

    @Override
    public List<Interpretation> react(Interpretation input) {
        return Lists.interpretationList(new Interpretation("I'm Roboy! I'm awesome! [random answer]"));
    }

    @Override
    public AbstractState getNextState() {
        return null; // this state is only used as fallback, no transitions needed
    }
}
