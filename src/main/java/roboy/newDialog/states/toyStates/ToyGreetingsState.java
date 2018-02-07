package roboy.newDialog.states.toyStates;

import roboy.newDialog.states.State;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.logic.StatementInterpreter;
import roboy.newDialog.states.StateParameters;
import roboy.talk.Verbalizer;

import java.util.*;

/**
 * ToyGreetingsState can be used as the initial state.
 * Roboy will greet the Interlocutor with "Hello".
 *
 * If the response is a greeting, the "next" transition is taken.
 * Otherwise the fallback will be triggered and the "noHello" transition is taken.
 *
 * Fallback is required.
 * Outgoing transitions that have to be defined:
 * - next:    following state if there was a greeting
 * - noHello: following state if there was NO greeting
 */
public class ToyGreetingsState extends State {

    private boolean inputOK = true;

    public ToyGreetingsState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

    @Override
    public Output act() {
        return Output.say(new Interpretation("Hello! [expecting greeting]"));
    }

    @Override
    public Output react(Interpretation input) {
        // react to input

        String sentence = (String) input.getFeatures().get(Linguistics.SENTENCE);
        inputOK = StatementInterpreter.isFromList(sentence, Verbalizer.greetings);

        if (inputOK) {
            return Output.say( new Interpretation("I like it when you greet me! [greeting detected, next state]") );
        } else {
            return Output.useFallback(); // -> fallback state will be used
        }
    }


    @Override
    public State getNextState() {
        if (inputOK) {
            return getTransition("next");

        } else {
            return getTransition("noHello");
            // alternatively: return `this` to repeat
        }
    }



    @Override
    protected Set<String> getRequiredTransitionNames() {
        // optional: define all required transitions here:
        return newSet("next", "noHello");
    }

    @Override
    public boolean isFallbackRequired() {
        // optional: indicate that this state requires fallback in some cases
        // for this state this happens if no greeting was detected
        return true;
    }
}
