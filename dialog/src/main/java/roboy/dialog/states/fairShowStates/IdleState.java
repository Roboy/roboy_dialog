package roboy.dialog.states.fairShowStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.dialog.states.definitions.MonologState;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;

import java.util.concurrent.TimeUnit;

/**
 * Idle state for the fair show personality.
 * Roboy is waiting <delay> minutes to autonomously start a conversation.
 *
 */
public class IdleState extends MonologState {

    private final static String TRANSITION_TIME_IS_UP = "timeIsUp";
    private final static String DELAY_ID = "delayInMins";
    private final static String SHOW_TIME_ID = "showTimeinMins";
    private final static int MIN_NUMBER_PEOPLE = 1;
    private final Logger LOGGER = LogManager.getLogger();

    private State nextState = this;
    private long delay;
    private long showTime;
    private long startTime;

    public IdleState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
        delay = Long.parseLong(params.getParameter(DELAY_ID));
        showTime = Long.parseLong(params.getParameter(SHOW_TIME_ID));
        LOGGER.info("--> Timer: " + delay + " mins in Idle-State.");
        LOGGER.info("--> Show takes: " + showTime + " mins.");
        startTime = System.nanoTime();
    }

    @Override
    public Output act() {

        if(TimeUnit.MINUTES.toNanos(showTime) < System.nanoTime() - startTime){
            LOGGER.info("Closing Conversation");
            return Output.endConversation();
        }

        LOGGER.info("Starting Idle State, waiting " + delay + " Minute(s) until next Interaction!");

        long enteringTime = System.nanoTime();

        //while(notInVision() && (TimeUnit.MINUTES.toNanos(delay) > System.nanoTime() - enteringTime))
        while(TimeUnit.MINUTES.toNanos(delay) > System.nanoTime() - enteringTime){
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage());
            }
        }

        nextState = getTransition(TRANSITION_TIME_IS_UP);

        return Output.sayNothing();
    }

    @Override
    public State getNextState() {
        return nextState;
    }

    private boolean notInVision(){

            try {

                return getContext().CROWD_DETECTION.getLastValue().getData() < MIN_NUMBER_PEOPLE;
            } catch(NullPointerException e){
                LOGGER.info("Make sure crowd detection is publishing, receiving: " + e.getMessage());
                return true;
            }
        }

}


