package roboy.dialog.states.fairShowStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.dialog.states.definitions.MonologState;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.talk.PhraseCollection;
import roboy.talk.Verbalizer;

import java.util.concurrent.TimeUnit;

/**
 * Idle state.
 * Roboy is waiting until he sees some person to autonomously start a conversation.
 *
 */
public class DemoIdleState extends MonologState {

    private final static String TRANSITION_PERSON_DETECTED = "personDetected";

    private final static String SHOW_TIME_ID = "showTimeinMins";
    private final Logger LOGGER = LogManager.getLogger();

    private State nextState = this;

    private long showTime;
    private long startTime;

    public DemoIdleState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
        showTime = Long.parseLong(params.getParameter(SHOW_TIME_ID));
        LOGGER.info("--> The show takes: " + showTime + " mins.");
        startTime = System.nanoTime();
    }

    @Override
    public Output act() {

        if(TimeUnit.MINUTES.toNanos(showTime) < System.nanoTime() - startTime){
            LOGGER.info("Closing Conversation after " + showTime + " minutes");
            return Output.endConversation();
        }

        while(notInVision()){
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage());
            }
        }

        nextState = getTransition(TRANSITION_PERSON_DETECTED);

        return Output.say(Verbalizer.greetings.getRandomElement() + " " + Verbalizer.roboyIntro.getRandomElement() + PhraseCollection.ROBOY_PHRASES.getRandomElement());
    }

    @Override
    public State getNextState() {
        return nextState;
    }

    private boolean notInVision(){

            try {

                return !getContext().PERSON_DETECTION.getLastValue().getData();
            } catch(NullPointerException e){
                LOGGER.info("Make sure person listening is publishing, receiving: " + e.getMessage());
                return true;
            }
        }

}


