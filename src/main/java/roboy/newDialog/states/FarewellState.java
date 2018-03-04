package roboy.newDialog.states;

import roboy.linguistics.sentenceanalysis.Interpretation;

import java.util.Set;

public class FarewellState extends State {

    public FarewellState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

    @Override
    public Output act() {
        return Output.say("FarewellState act()");
    }

    @Override
    public Output react(Interpretation input) {
        return Output.say("FarewellState react()");
    }

    @Override
    public State getNextState() {
        return null;
    }

    @Override
    protected Set<String> getRequiredTransitionNames() {
        return newSet();
    }

    @Override
    protected Set<String> getRequiredParameterNames() {
        return newSet();
    }

    @Override
    public boolean isFallbackRequired() {
        return false;
    }
}
