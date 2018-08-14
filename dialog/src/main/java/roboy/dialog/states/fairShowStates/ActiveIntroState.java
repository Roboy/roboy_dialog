package roboy.dialog.states.fairShowStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.dialog.states.definitions.MonologState;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.talk.PhraseCollection;
import roboy.talk.Verbalizer;



/**
 * Active state to start a conversation.
 * Roboy is introducing himself autonomously
 *
 */
public class ActiveIntroState extends MonologState {

    private final static String TRANSITION_PEOPLE_AROUND = "peopleAround";
    private final static String TRANSITION_LONELY_ROBOY = "lonelyRoboy";
    private final int MIN_NUMBER_PEOPLE = 2;

    private final Logger logger = LogManager.getLogger();

    private State nextState = this;

    public ActiveIntroState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

    @Override
    public Output act() {

        return Output.say(Verbalizer.greetings.getRandomElement() + " " + Verbalizer.roboyIntro.getRandomElement() + PhraseCollection.ROBOY_PHRASES.getRandomElement());
    }

    @Override
    public State getNextState() {

        if(checkPplAround()){
            nextState = getTransition(TRANSITION_PEOPLE_AROUND);
        }else{
            nextState = getTransition((TRANSITION_LONELY_ROBOY));
        }

        return nextState;
    }


    private boolean checkPplAround(){

        try {

            return getContext().CROWD_DETECTION.getLastValue().getData() >= MIN_NUMBER_PEOPLE;
        } catch(NullPointerException e){
            logger.info("Make sure crowd detection publishing, receiving: " + e.getMessage());
            return false;
        }
    }

}
