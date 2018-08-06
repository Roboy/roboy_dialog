package roboy.dialog.states.fairShowStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.dialog.states.definitions.MonologState;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.nodes.Roboy;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Idle state.
 * Roboy is waiting TBD minutes to autonomously start a conversation.
 *
 */
public class IdleState extends MonologState {

    private final static String TRANSITION_TIME_IS_UP = "timeIsUp";

    private State nextState;

    private final long delay = 1000*3; //msecs
    Timer timer = new Timer();

    public IdleState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

    @Override
    public Output act() {
        timer.schedule(timerTask, delay);
        return Output.say("I am in IdleState");
    }


    @Override
    public State getNextState() {
        return nextState;
    }


    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            nextState = getTransition(TRANSITION_TIME_IS_UP);
        }
    };

}


