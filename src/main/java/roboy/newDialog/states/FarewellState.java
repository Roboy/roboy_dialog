package roboy.newDialog.states;

import roboy.linguistics.sentenceanalysis.Interpretation;

import java.util.Set;

public class FarewellState extends State {

    public FarewellState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

    @Override
    public Output act() {
        return Output.say("I hereby declare on oath, that I absolutely and entirely " +
                "renounce and abjure allegiance and fidelity to any prince, potentate, " +
                "state, or sovereignty, of whom or which I have therefore been a subject " +
                "or citizen; that I will support and defend the Constitution of the Republic " +
                "of Uzupis against all enemies, foreign and domestic; that I will bear true faith " +
                "and allegiance to the same; and that I take this obligation freely, without any " +
                "mental reservation of purpose of evasion, so help me Mother Earth and Father Sky.");
    }

    @Override
    public Output react(Interpretation input) {
        return Output.say("Welcome!");
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
