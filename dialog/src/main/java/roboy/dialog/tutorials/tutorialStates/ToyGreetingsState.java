package roboy.dialog.tutorials.tutorialStates;

import roboy.dialog.Segue;
import roboy.dialog.states.definitions.State;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.dialog.states.definitions.StateParameters;
import roboy.logic.StatementInterpreter;
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

    // used to remember which state to take next
    private State next;

    public ToyGreetingsState(String stateIdentifier, StateParameters params) {
        // nothing to initialize in constructor: just pass the parameters to the super class (State)
        super(stateIdentifier, params);
    }

    @Override
    public Output act() {

        // let's greet the person
        return Output.say("Hello! [expecting greeting]")
        // and add a random segue of type FLATTERY with probability of 0.7
            .setSegue(new Segue(Segue.SegueType.FLATTERY, 0.7));

        // Note:    When using Output.say(...), you can return an output based on
        //          a string or pass an interpretation object:
        //          return Output.say(new Interpretation("Hello! [expecting greeting]"));
    }

    @Override
    public Output react(Interpretation input) {
        // react to input

        String sentence = input.getSentence();

        if (sentence != null) {
            boolean inputOK = StatementInterpreter.isFromList(sentence, Verbalizer.greetings);

            if (inputOK) {
                next = getTransition("next");
                return Output.say("I like it when you greet me! [greeting detected, next state]");
            }
        }
        // the case where we don't get a greeting back
        // set another state as the next one
        next = getTransition("noHello");
        // don't think about the reply here, let the fallback state handle it
        return Output.useFallback(); // -> fallback state will be used
    }


    @Override
    public State getNextState() {
        return next;
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
