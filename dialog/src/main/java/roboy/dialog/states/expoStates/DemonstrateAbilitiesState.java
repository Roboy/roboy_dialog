package roboy.dialog.states.expoStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.ros.RosMainNode;
import roboy.talk.PhraseCollection;
import roboy.util.RandomList;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static roboy.emotions.RoboyEmotion.*;

/**
 * Implementations of Roboy's abilities.
 *
 * Following methods have to be implemented:
 *  - wouldYouLikeToSeeDemoQuestions() -  provides a list of yes/no questions for the act method
 *  - demonstrateAbility()             -  implementation of the ability (should block until demonstration is finished)
 *  - afterDemoEndedPhrases()          -  provides a list of phrases that wrap up the ability demonstration
 *
 */
enum RoboyAbility {

//    SHAKE_HAND {
//
//        @Override
//        public RandomList<String> wouldYouLikeToSeeDemoQuestions() {
//            return new RandomList<>("I have learned that humans sometimes shake hands when they meet. I very " +
//                    "much enjoy doing it too. Would you like to make our friendship official?");
//        }
//
//        @Override
//        public void demonstrateAbility(RosMainNode node) {
//            // TODO: MOVE HAND AND WAIT A BIT
//            logger.info("moving hand");
//            try {
//                node.PerformMovement("shoulder_right", "shanke_hand");
//            }
//            catch ( InterruptedException e) {
//                logger.error(e.getMessage());
//            }
//        }
//
//        @Override
//        public RandomList<String> afterDemoEndedPhrases() {
//            return new RandomList<>("Now I feel even more human! This hand is totally worth it. The roboy student team worked hard on it.");
//        }
//
//    },

    SHOW_HIP {

        @Override
        public RandomList<String> wouldYouLikeToSeeDemoQuestions() {
            return new RandomList<>("In the future, I will be designing myself. I have started with the hip already, done by AI.");
        }

        @Override
        public void demonstrateAbility(RosMainNode node) {
            // TODO: MOVE HAND AND WAIT A BIT
            logger.info("talking about the hip");
            node.ShowEmotion("url:https://www.tctmagazine.com/downloads/8332/download/Autodesk-logo.png?cb=05a923bcee4fbbc61e67476114315d4c&w=480");

        }

        @Override
        public RandomList<String> afterDemoEndedPhrases() {
            return new RandomList<>("Is that not the hippest hip you have ever seen?");
        }

    },

//    RECOGNIZE_OBJECTS {
//
//        @Override
//        public RandomList<String> wouldYouLikeToSeeDemoQuestions() {
//            return new RandomList<>("I have a new vision system! Do you want to know which objects I currently see?");
//        }
//
//        private String recognizedObjects = "nothing";
//        @Override
//        public void demonstrateAbility() {
//            // TODO: GET A LIST OF RECOGNIZED OBJECTS ADN SAVE IT IN recognizedObjects
//            recognizedObjects = "nothing";
//
//            logger.info("recognizing objects");
//        }
//
//        @Override
//        public RandomList<String> afterDemoEndedPhrases() {
//            return new RandomList<>("I currently can see " + recognizedObjects);
//        }
//
//    },

    SHOW_EMOTION {

        @Override
        public RandomList<String> wouldYouLikeToSeeDemoQuestions() {
            return new RandomList<>("I am just like you are. Wanna know what I mean?");
        }


        @Override
        public void demonstrateAbility(RosMainNode node) {
            // TODO: SHOW AN EMOTION
            try {
                logger.info("showing emotions");
                node.SynthesizeSpeech("I can be all flirty...");
                TimeUnit.SECONDS.sleep(1);
                node.ShowEmotion(SMILE_BLINK);
                TimeUnit.SECONDS.sleep(1);
                node.SynthesizeSpeech("But sometimes I get shy.");
                node.ShowEmotion(SHY);
                TimeUnit.SECONDS.sleep(1);
                node.SynthesizeSpeech("You can can check out my facebook page");
                TimeUnit.SECONDS.sleep(1);
                node.ShowEmotion(FACEBOOK_EYES);
                TimeUnit.SECONDS.sleep(1);
                node.SynthesizeSpeech("And if you do, you can give you a kiss. Muah");
                TimeUnit.SECONDS.sleep(1);
                node.ShowEmotion(KISS);
                TimeUnit.SECONDS.sleep(1);
            }catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
        }

        @Override
        public RandomList<String> afterDemoEndedPhrases() {
            return new RandomList<>("Can you guess my feelings?");
        }

    },

    MOVE_BODY {

        @Override
        public RandomList<String> wouldYouLikeToSeeDemoQuestions() {
            return new RandomList<>("My biggest dream is to have a body as good as the human body. " +
                    "I can already move a little bit. Would you like to see it?");
        }

        @Override
        public void demonstrateAbility(RosMainNode node) {
            // TODO: MOVE SOME BODY PARTS
            logger.info("moving body");
            List<String> parts = Arrays.asList("shoulder_left", "spine_right");
            String name = "random1";
            try {
                for (int i=0; i<parts.size();i++) {
                    node.PerformMovement(parts.get(i), name);
                }
            }catch (InterruptedException e) {
                logger.error(e.getMessage());
            }

        }

        @Override
        public RandomList<String> afterDemoEndedPhrases() {
            return new RandomList<>("Trust me, moving is not easy at all but I am getting better and better at it.");
        }

    };

    private final static Logger logger = LogManager.getLogger();

    /**
     * List of phrases for the act method. Every phrase should ask the interlocutor
     * whether he wants to see the ability in action.
     * @return list of phrases for the act method
     */
    public abstract RandomList<String> wouldYouLikeToSeeDemoQuestions();

    /**
     * Implementation of the ability. This method should block until the ability demonstration is finished.
     */
    public abstract void demonstrateAbility(RosMainNode node);

    /**
     * List of phrases that wrap up the ability demonstration.
     * One of these phrases will be used after the demonstration is finished.
     * @return list of phrases that wrap up the ability demonstration
     */
    public abstract RandomList<String> afterDemoEndedPhrases();

}


/**
 * This state asks the interlocutor whether Roboy should demonstrate one of his abilities.
 * The abilities include: shake hand, recognize objects, show emotion, move body
 *
 * Every time this state is entered, Roboy picks one of the abilities (that haven't been demonstrated yet)
 * and asks the interlocutor whether it should be demonstrated. If all abilities were already demonstrated,
 * one is chosen at random. The ability is demonstrated only if the interlocutor said yes (or similar).
 *
 * Control flow:
 *  - act():     "Would you like to see me doing {ability}?"
 *  - listen()
 *  - react():
 *     - if answer = yes: demonstrate ability, say final remark, take 'abilityWasDemonstrated' transition
 *     - otherwise:       skip ability, say another final remark, take 'abilityDemonstrationSkipped' transition
 *
 *
 * DemonstrateAbilitiesState interface:
 * 1) Fallback is not required.
 * 2) Outgoing transitions that have to be defined:
 *    - abilityWasDemonstrated:         following state if the ability was demonstrated
 *    - abilityDemonstrationSkipped:    following state if the ability demonstration was skipped
 * 3) No parameters are used.
 */
public class DemonstrateAbilitiesState extends State {

    private final static String TRANS_ABILITY_DEMONSTRATED = "abilityWasDemonstrated";
    private final static String TRANS_ABILITY_SKIPPED = "abilityDemonstrationSkipped";

    private final RandomList<RoboyAbility> availableAbilities = new RandomList<>();
    private RoboyAbility activeAbility;

    private State nextState;
    private final Logger logger = LogManager.getLogger();

    public DemonstrateAbilitiesState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
        resetAvailableAbilities();
    }

    @Override
    public Output act() {
        activeAbility = selectRandomAbility();
        String wouldYouLikeToSeeDemoQuestion = activeAbility.wouldYouLikeToSeeDemoQuestions().getRandomElement();
        return Output.say(wouldYouLikeToSeeDemoQuestion);
    }

    @Override
    public Output react(Interpretation input) {

        Linguistics.UtteranceSentiment inputSentiment = getInference().inferSentiment(input);
        if (inputSentiment == Linguistics.UtteranceSentiment.NEGATIVE) {
            // not positive -> skip this ability
            nextState = getTransition(TRANS_ABILITY_SKIPPED);

            return Output.say(PhraseCollection.NEGATIVE_SENTIMENT_PHRASES.getRandomElement());
        }

        // select next state
        nextState = getTransition(TRANS_ABILITY_DEMONSTRATED);

        // demonstrate ability
        activeAbility.demonstrateAbility(getRosMainNode());

        // wrap up with final remark
        String afterDemoEndedPhrase = activeAbility.afterDemoEndedPhrases().getRandomElement();
        return Output.say(afterDemoEndedPhrase);
    }

    @Override
    public State getNextState() {
        return nextState;
    }


    /**
     * Resets the list of available abilities so that it contains all of them.
     */
    private void resetAvailableAbilities() {
        availableAbilities.clear();
        availableAbilities.addAll(Arrays.asList(RoboyAbility.values()));
    }

    /**
     * Selects one of the abilities from the availableAbilities list at random and removes it from the list.
     * If the list becomes empty this way, resets it to the initial state
     * @return one of the available abilities
     */
    private RoboyAbility selectRandomAbility() {
        RoboyAbility ability = availableAbilities.getRandomElement();
        availableAbilities.remove(ability);
        if (availableAbilities.size() == 0) {
            resetAvailableAbilities(); // reset if all abilities were used
            logger.info("all abilities were selected at least once, resetting the list");
        }
        return ability;
    }




    //////////  STATE INTERFACE

    @Override
    protected Set<String> getRequiredTransitionNames() {
        return newSet(TRANS_ABILITY_DEMONSTRATED, TRANS_ABILITY_SKIPPED);
    }
}
