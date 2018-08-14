package roboy.dialog.states.fairShowStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.dialog.states.definitions.MonologState;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.emotions.RoboyEmotion;
import roboy.memory.Neo4jMemory;
import roboy.memory.nodes.MemoryNodeModel;
import roboy.memory.nodes.Roboy;
import roboy.ros.RosMainNode;
import roboy.talk.PhraseCollection;
import roboy.util.RandomList;

import java.util.*;



enum InfoAbout {

    ABOUT_TEAM {

        private RandomList<String> phrases = PhraseCollection.ROBOY_TEAM_PHRASES;

        @Override
        public State.Output performSpecialAction(RosMainNode rosNode){

            return State.Output.say(phrases.getRandomElement());
        }

    },
    ABOUT_ROBOY {

        private RandomList<String> phrases = PhraseCollection.MISSION_PHRASES;
        @Override
        public State.Output performSpecialAction(RosMainNode rosNode){

            return State.Output.say(phrases.getRandomElement());
        }
    },
    ABOUT_MOVEMENT {


        Random random    = new Random();
        RandomList<String> phrases = PhraseCollection.MOVEMENT_PHRASES;

        @Override
        public State.Output performSpecialAction(RosMainNode rosNode){
            //TODO: what can be moved?
            HashMap<String, String> parts = new HashMap<>();
            parts.put("shoulder_left", "left shoulder");
            parts.put("shoulder_right", "right shoulder");
            parts.put("spine_right", "right leg");
            parts.put("spine_left", "left leg");

            List<String> bodyParts = new ArrayList<>(parts.keySet());

            String randomPart = bodyParts.get(random.nextInt(bodyParts.size()));
            String randomPartLiteral = parts.get(randomPart);

            try {
                //TODO: what is 2nd parameter?
                rosNode.PerformMovement(randomPart, "random1");

            }catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
            logger.info("synthesising: " + String.format(phrases.getRandomElement(), randomPartLiteral));
            return State.Output.say(String.format(phrases.getRandomElement(), randomPartLiteral));
        }
    },
    ABOUT_EMOTIONS {

        private RandomList<String> phrases = PhraseCollection.EMOTION_PHRASES;
        private RandomList<RoboyEmotion> emotions = new RandomList<>();


        @Override
        public State.Output performSpecialAction(RosMainNode rosNode){

            emotions.addAll(Arrays.asList(RoboyEmotion.values()));

            return State.Output.say(phrases.getRandomElement()).setEmotion(emotions.getRandomElement());
        }
    };

    private final static Logger logger = LogManager.getLogger();

    public abstract State.Output performSpecialAction(RosMainNode rosNode);

}

/**
 * Roboy is talking about several topics autonomously
 * - team
 * - mission
 * - movement
 * - emotions
 */
public class InfoTalkState extends MonologState {

    private final static String TRANSITION_PERSON_DETECTED = "personDetected";
    private final static String TRANSITION_LONELY_ROBOY = "lonelyRoboy";

    private final RandomList<InfoAbout> availableInformation = new RandomList<>();
    private InfoAbout activeInfo;

    private final Logger logger = LogManager.getLogger();

    private State nextState = this;

    public InfoTalkState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
        resetAvailableInformation();
    }

    @Override
    public Output act() {

        activeInfo = selectRandomInfo();

        return activeInfo.performSpecialAction(getRosMainNode());
    }

    @Override
    public State getNextState() {

        if(checkPplListening()){
            nextState = getTransition(TRANSITION_PERSON_DETECTED);
        } else {
            nextState = getTransition(TRANSITION_LONELY_ROBOY);
        }

        return nextState;
    }


    /**
     * Resets the list of available information so that it contains all of them.
     */
    private void resetAvailableInformation() {
        availableInformation.clear();
        availableInformation.addAll(Arrays.asList(InfoAbout.values()));
    }

    /**
     * Selects one of the pieces of information from the availableInformation list at random and removes it from the list.
     * If the list becomes empty this way, resets it to the initial state
     * @return one of the available pieces of information
     */
    private InfoAbout selectRandomInfo() {
        InfoAbout infoAbout = availableInformation.getRandomElement();
        availableInformation.remove(infoAbout);
        if (availableInformation.size() == 0) {
            resetAvailableInformation(); // reset if all infos were used
            logger.info("all pieces of information were selected at least once, resetting the list");
        }
        return infoAbout;
    }

    /**
     * checks if vision module detects a person that is interested
     * @return boolean if someone is interested
     */

    private boolean checkPplListening(){

        try {

            return getContext().PERSON_DETECTION.getLastValue().getData();
        }catch(NullPointerException e){
            logger.info("Make sure person detection is publishing, receiving: " + e.getMessage());
            return false;
        }
    }

}
