package roboy.dialog.tutorials.tutorialStates;

import roboy.dialog.states.definitions.State;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.dialog.states.definitions.StateParameters;

import java.util.*;

/**
 * ToyIntroState demonstrates a simple introduction. A single parameter is used.
 * Roboy will tell the interlocutor his name and ask for the Interlocutor's name.
 * The reply is ignored to keep this example simple.
 *
 * ToyIntroState interface:
 * 1) Fallback is not required.
 * 2) Outgoing transitions that have to be defined:
 *    - next:    following state
 * 3) Names of the parameters that have to be passed to the constructor:
 *    - introductionSentence:    A sentence that should be used as introduction
 */
public class ToyIntroState extends State {


    public ToyIntroState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

    @Override
    public Output act() {
        String introSentenceFromParams = getParameters().getParameter("introductionSentence");
        return Output.say( introSentenceFromParams +" (<--- defined as parameter) Who are you? [say anything]");
    }

    @Override
    public Output react(Interpretation input) {
        return Output.say("Nice to meet you! [moving to next state]");
    }

    @Override
    public State getNextState() {
        return getTransition("next");
    }

    @Override
    protected Set<String> getRequiredTransitionNames() {
        // optional: define all required transitions here:
        return newSet("next");
    }

    @Override
    protected Set<String> getRequiredParameterNames() {
        return newSet("introductionSentence");
    }
}
