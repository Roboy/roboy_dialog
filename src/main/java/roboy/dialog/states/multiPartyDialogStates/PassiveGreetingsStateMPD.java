package roboy.dialog.states.multiPartyDialogStates;

import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.logic.StatementInterpreter;
import roboy.talk.Verbalizer;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Set;

/**
 * Passive state to start a conversation.
 * Roboy is waiting until a greeting or his name is detected.
 *
 */

//TODO The Input will change!!! Changes need to be followed through here!!! 

public class PassiveGreetingsStateMPD extends State {

    private final String TRANSITION_GREETING_DETECTED = "greetingDetected";

    private State next;

    public PassiveGreetingsStateMPD(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
        next = this;
    }

    @Override
    public Output act() {

        return Output.sayNothing();
    }

    @Override
    public Output react(Interpretation input) {
        return null;
    }


    public Output react(ArrayList<Interpretation> input) {
        /*
        String sentence = (String) input.getFeatures().get(Linguistics.SENTENCE);
        boolean inputOK = StatementInterpreter.isFromList(sentence, Verbalizer.greetings) ||
                StatementInterpreter.isFromList(sentence, Verbalizer.roboyNames) ||
                StatementInterpreter.isFromList(sentence, Verbalizer.triggers);

        if (inputOK) {
            next = getTransition(TRANSITION_GREETING_DETECTED);
            return Output.say(Verbalizer.greetings.getRandomElement());
        }
*/
        return Output.sayNothing();
    }

    @Override
    public State getNextState() {
        return next;
    }

    @Override
    protected Set<String> getRequiredTransitionNames() {
        return newSet(TRANSITION_GREETING_DETECTED);
    }

    @Override
    protected Set<String> getRequiredParameterNames() {
        return newSet(); // empty set
    }

    @Override
    public boolean isFallbackRequired() {
        return false;
    }
}
