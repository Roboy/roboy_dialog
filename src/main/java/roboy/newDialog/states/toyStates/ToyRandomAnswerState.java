package roboy.newDialog.states.toyStates;

import roboy.newDialog.states.State;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.util.Lists;

import java.util.List;

public class ToyRandomAnswerState extends State {

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
    public State getNextState() {
        return null; // this state is only used as fallback, no transitions needed
    }
}
