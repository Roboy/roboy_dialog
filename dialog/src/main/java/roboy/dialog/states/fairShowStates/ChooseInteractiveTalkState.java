package roboy.dialog.states.fairShowStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.dialog.states.definitions.MonologState;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.util.RandomList;

import java.util.Arrays;

/**
 * State to randomly select the next interactive state
 * This State will
 * - first switch state to get to know the person if he/she is unknown
 * - check for detected objects from vision -> switch state to talk about these
 * - switch state to play games, calculate mathematical stuff or do question answering
 */
public class ChooseInteractiveTalkState extends MonologState {

    private final static String TRANSITION_NEW_PERSON = "newPerson";
    private final static String TRANSITION_MATH = "math";
    private final static String TRANSITION_GAME = "game";
    private final static String TRANSITION_PERSONAL_QA = "personalQA";
    private final static String TRANSITION_OBJECT_DETECTION = "objectDetected";
    private final static String TRANSITION_GENERAL_QA = "generalQA";

    private final Logger logger = LogManager.getLogger();

    private State nextState = this;

    private final RandomList<String> availableInteractions = new RandomList<>();
    private String nextInteraction;


    public ChooseInteractiveTalkState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
        resetAvailableInteractions();
    }

    @Override
    public Output act() {

        if(getContext().ACTIVE_INTERLOCUTOR.getValue().getName() == null){

            nextInteraction = TRANSITION_NEW_PERSON;

        } else {

            nextInteraction = selectRandomInteraction();

        }
        nextState = getTransition(nextInteraction);

        return Output.sayNothing();
    }


    @Override
    public State getNextState() {
        return nextState;
    }

    /**
     * Resets the list of available interactions so that it contains all of them.
     */
    private void resetAvailableInteractions() {
        availableInteractions.clear();
        availableInteractions.addAll(Arrays.asList(TRANSITION_PERSONAL_QA, TRANSITION_MATH, TRANSITION_GAME, TRANSITION_OBJECT_DETECTION, TRANSITION_GENERAL_QA));
    }

    /**
     * Selects one of the interactions from the availableInteractions list at random and removes it from the list.
     * If the list becomes empty this way, resets it to the initial state
     * @return one of the available interactions
     */
    private String selectRandomInteraction() {
        String interaction = availableInteractions.getRandomElement();
        availableInteractions.remove(interaction);
        if (availableInteractions.size() == 0) {
            resetAvailableInteractions(); // reset if all infos were used
            logger.info("all interactions were selected at least once, resetting the list");
        }
        return interaction;
    }


}
