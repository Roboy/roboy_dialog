package roboy.dialog.states.fairShowStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.dialog.states.definitions.MonologState;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.talk.Verbalizer;

/**
 * Passive state to start a conversation.
 * Roboy is introducing himself autonomously
 *
 */
public class SocialMediaState extends MonologState {

    private final static String TRANSITION_FINISH = "finished";

    private final Logger LOGGER = LogManager.getLogger();

    private State nextState;

    public SocialMediaState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

    @Override
    public Output act() {

        return Output.say("I am in Social Media State");
    }


    @Override
    public State getNextState() {
        return getTransition(TRANSITION_FINISH);
    }

}
