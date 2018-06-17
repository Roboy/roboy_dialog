package roboy.dialog.tutorials.tutorialStates;

import roboy.dialog.states.definitions.State;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.dialog.states.definitions.StateParameters;

/**
 * This state is meant to be used as a fallback-only state. It only implements the react() function
 * returning a hardcoded random answer. The react function of this state will be used if another state
 * can't react and requires a fallback.
 *
 * This state should never become active (meaning that no transition should point to it.)
 *
 * ToyRandomAnswerState interface:
 * 1) Fallback is not required (this state should be the fallback).
 * 2) This state has no outgoing transitions.
 * 3) No parameters are used.
 */
public class ToyRandomAnswerState extends State {

    public ToyRandomAnswerState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

    @Override
    public Output act() {
        return Output.sayNothing(); // this state is only used as fallback, no act needed
    }

    @Override
    public Output react(Interpretation input) {

        // This function will be called if another state doesn't know how to react
        // and this state is attached as a fallback to it.

        // We could try to say something intelligent based on the input here
        // but for now, let's just return a simple string.

        return Output.say("I'm Roboy! I'm awesome! [random answer]");
    }

    @Override
    public State getNextState() {
        return null; // this state is only used as fallback, no next state
    }
}
