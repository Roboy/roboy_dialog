package roboy.dialog.states.fairShowStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.dialog.states.definitions.MonologState;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;

import java.util.concurrent.TimeUnit;

/**
 * Idle state.
 * Roboy is waiting <delay> minutes to autonomously start a conversation.
 *
 */
public class IdleState extends MonologState {

    private final static String TRANSITION_TIME_IS_UP = "timeIsUp";
    private final static String DELAY_ID = "delayInMins";
    private final Logger LOGGER = LogManager.getLogger();

    private State nextState = this;
    private long delay;

    public IdleState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
        delay = Long.parseLong(params.getParameter(DELAY_ID));
        LOGGER.info("--> Timer: " + delay + " mins in Idle-State");
    }

    @Override
    public Output act() {

        try {
            TimeUnit.SECONDS.sleep(6);
            //TimeUnit.MINUTES.sleep(delay);
            nextState = getTransition(TRANSITION_TIME_IS_UP);
        }
        catch(InterruptedException e){
            LOGGER.error("--> Unable to pause Conversation in Idle State " + e.getMessage());
        }

        return Output.sayNothing();
    }

    @Override
    public State getNextState() {
        return nextState;
    }

}


