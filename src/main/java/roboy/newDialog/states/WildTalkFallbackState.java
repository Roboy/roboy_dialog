package roboy.newDialog.states;

import roboy.linguistics.sentenceanalysis.Interpretation;

import java.util.Set;

public class WildTalkFallbackState extends State {

    public WildTalkFallbackState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

    @Override
    public Output act() {
        return Output.say("WildTalkFallbackState should never act!");
    }

    @Override
    public Output react(Interpretation input) {
        return null;
    }

    @Override
    public State getNextState() {
        // no next state for fallback states
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
