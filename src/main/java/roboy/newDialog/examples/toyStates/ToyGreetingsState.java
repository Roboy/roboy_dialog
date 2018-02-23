package roboy.newDialog.examples.toyStates;

import roboy.newDialog.states.State;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.logic.StatementInterpreter;
import roboy.newDialog.states.StateParameters;
import roboy.talk.Verbalizer;

import java.util.*;

/**
 * This is a very simple example how you could implement an initial state.
 * Roboy will greet the interlocutor (the guy he speaks to) with "Hello".
 *
 * If the response is a greeting, the "next" transition is taken.
 * Otherwise the fallback will be triggered and the "noHello" transition is taken.
 *
 * ToyGreetingsState interface:
 * 1) Fallback is required.
 * 2) Outgoing transitions that have to be defined:
 *    - next:    following state if there was a greeting
 *    - noHello: following state if there was NO greeting
 * 3) No parameters are used.
 */
public class ToyGreetingsState extends State {

    // used to remember whether the response was a greeting
    private boolean inputOK = true;

    public ToyGreetingsState(String stateIdentifier, StateParameters params) {
        // nothing to initialize in constructor: just pass the parameters to the super class (State)
        super(stateIdentifier, params);
    }

    @Override
    public Output act() {
        return Output.say("Hello there people! I am Roboy. Good evening!");
        // you can return an output based on a string or pass an interpretation object:
        // return Output.say(new Interpretation("Hello! [expecting greeting]"));
    }

    @Override
    public Output react(Interpretation input) {
        // react to input

        String sentence = (String) input.getFeatures().get(Linguistics.SENTENCE);
        inputOK = StatementInterpreter.isFromList(sentence, Verbalizer.greetings);

        if (inputOK) {
            return Output.say( "I like it when you greet me. "  +
                    "Oh, it's so great that you want to become a citizen of the Republic of Uzupis! " +
                            "Soon you will be a full-fledged citizen of the Unesco world cultural heritage of Uzupis, " +
                            "the artist republic, the republic of angles and the republic of apples. So let's get started! " +
                    "I need to collect some information to make a decision." );
        } else {
            // the case where we don't get a greeting back
            // don't worry about this here, everything will be handled by the fallback state
            return Output.useFallback(); // -> fallback state will be used
        }
    }


    @Override
    public State getNextState() {
        if (inputOK) {
            return getTransition("next");

        } else {
            return getTransition("noHello");
            // alternatively: return `this` to stay in this state until a greeting is detected
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

    // optional: if we would use parameters here, we could define the names inside
    // Set<String> getRequiredParameterNames()
    // for this state it is not necessary

}
