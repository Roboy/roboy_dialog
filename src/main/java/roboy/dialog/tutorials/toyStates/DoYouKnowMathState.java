package roboy.dialog.tutorials.toyStates;

import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.sentenceanalysis.Interpretation;

public class DoYouKnowMathState extends State {

    public DoYouKnowMathState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

    @Override
    public Output act() {
        return null;
    }

    @Override
    public Output react(Interpretation input) {
        return null;
    }

    @Override
    public State getNextState() {
        return null;
    }
}
