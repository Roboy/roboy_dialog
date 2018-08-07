package roboy.dialog.states.fairShowStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.dialog.states.definitions.MonologState;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Idle state.
 * Roboy is waiting <delay> milliseconds to autonomously start a conversation.
 *
 */
public class IdleState extends MonologState {

    private final static String TRANSITION_TIME_IS_UP = "timeIsUp";
    private final Logger LOGGER = LogManager.getLogger();

    private State nextState = this;

    private final long delay = 1000 * 5; //msecs
    Timer timer = new Timer();

    public IdleState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
        timer.schedule(exitState, delay);
        LOGGER.info("--> started Timer for " + delay + " msecs in Idle-State");
    }

    @Override
    public Output act() {

        return Output.sayNothing();
    }

    @Override
    public State getNextState() {
        return nextState;
    }


    TimerTask exitState = new TimerTask() {
        @Override
        public void run() {
            nextState = getTransition(TRANSITION_TIME_IS_UP);
            timer.cancel();
            LOGGER.info("--> stopped Timer in Idle-State");
        }
    };

}


