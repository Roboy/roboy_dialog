package roboy.newDialog.examples.toyStates;

import roboy.newDialog.states.State;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.newDialog.states.StateParameters;

import java.util.*;

/**
 * ToyIntroState demonstrates a simple introduction. A single parameter is used.
 * Roboy will tell the Interlocutor his name and ask for the Interlocutor's name.
 * The reply is ignored.
 *
 * Fallback is not required.
 * Outgoing transitions that have to be defined:
 * - next:    following state
 */
public class ToyIntroState extends State {


    public ToyIntroState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

    @Override
    public Output act() {
        String introSentenceFromParams = getParameters().getParameter("introductionSentence");
        return Output.say(new Interpretation( introSentenceFromParams +" (<--- defined as parameter) Who are you? [say anything]"));
    }

    @Override
    public Output react(Interpretation input) {
        return Output.say(new Interpretation("Nice to meet you! [moving to next state]"));
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
