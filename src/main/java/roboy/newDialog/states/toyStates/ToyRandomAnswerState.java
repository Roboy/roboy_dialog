package roboy.newDialog.states.toyStates;

import roboy.newDialog.states.State;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.util.Lists;

import java.util.List;

/**
 * ToyRandomAnswerState is meant to be used as a fallback state.
 * It only implements the react() function returning a hardcoded random answer.
 * This state should never become active (meaning that no transition should point to it.)
 *
 * Fallback is not required (this state should be the fallback).
 * This state has no outgoing transitions.
 */
public class ToyRandomAnswerState extends State {

    public ToyRandomAnswerState(String stateIdentifier) {
        super(stateIdentifier);
    }

    @Override
    public Interpretation act() {
        return null; // this state is only used as fallback, no act needed
    }

    @Override
    public Interpretation react(Interpretation input) {
        return new Interpretation("I'm Roboy! I'm awesome! [random answer]");
    }

    @Override
    public State getNextState() {
        return null; // this state is only used as fallback, no transitions needed
    }
}
