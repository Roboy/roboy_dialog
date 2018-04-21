package roboy.dialog.states.expoStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.util.RandomList;

import java.util.Arrays;
import java.util.Set;

/**
 * Implementations of Roboy's abilities.
 *
 * Following methods have to be implemented:
 *  - wouldYouLikeToSeeDemoQuestions() -  provides a list of phrases for the act method
 *  - demonstrateAbility()             -  implementation of the ability (should block until demonstration is finished)
 *  - afterDemoEndedPhrases()          -  provides a lList of phrases that wrap up the ability demonstration
 *
 */
enum RoboyAbility {

    SHAKE_HAND {

        @Override
        public RandomList<String> wouldYouLikeToSeeDemoQuestions() {
            return new RandomList<>("My hand is very cool! Should I move it so you can shake it?");
        }

        @Override
        public void demonstrateAbility() {
            // TODO: MOVE HAND AND WAIT A BIT
            logger.info("moving hand");
        }

        @Override
        public RandomList<String> afterDemoEndedPhrases() {
            return new RandomList<>("I hope you like my hand! The roboy student team worked hard on it.");
        }

    },

    RECOGNIZE_OBJECTS {

        @Override
        public RandomList<String> wouldYouLikeToSeeDemoQuestions() {
            return new RandomList<>("I have a new vision system! Do you want to know which objects I currently see?");
        }

        private String recognizedObjects = "nothing";
        @Override
        public void demonstrateAbility() {
            // TODO: GET A LIST OF RECOGNIZED OBJECTS ADN SAVE IT IN recognizedObjects
            recognizedObjects = "nothing";

            logger.info("recognizing objects");
        }

        @Override
        public RandomList<String> afterDemoEndedPhrases() {
            return new RandomList<>("I currently can see " + recognizedObjects);
        }

    },

    SHOW_EMOTION {

        @Override
        public RandomList<String> wouldYouLikeToSeeDemoQuestions() {
            return new RandomList<>("My face can show a lot of different emotions, would you like to see one of them?");
        }


        @Override
        public void demonstrateAbility() {
            // TODO: SHOW AN EMOTION
            logger.info("showing emotions");
        }

        @Override
        public RandomList<String> afterDemoEndedPhrases() {
            return new RandomList<>("Can you guess my feelings?");
        }

    },

    MOVE_BODY {

        @Override
        public RandomList<String> wouldYouLikeToSeeDemoQuestions() {
            return new RandomList<>("My biggest dream is to become as good as a human. " +
                    "I can already move a little bit. Would you like to see it?");
        }

        @Override
        public void demonstrateAbility() {
            // TODO: MOVE SOME BODY PARTS
            logger.info("moving body");
        }

        @Override
        public RandomList<String> afterDemoEndedPhrases() {
            return new RandomList<>("Can you guess my feelings?");
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
    public abstract void demonstrateAbility();

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
        if (inputSentiment != Linguistics.UtteranceSentiment.POSITIVE) {
            // not positive -> skip this ability
            nextState = getTransition(TRANS_ABILITY_SKIPPED);
            return Output.say("OK, let's talk about something else. ");
        }

        // select next state
        nextState = getTransition(TRANS_ABILITY_DEMONSTRATED);

        // demonstrate ability
        activeAbility.demonstrateAbility();

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
        }
        return ability;
    }




    //////////  STATE INTERFACE

    @Override
    protected Set<String> getRequiredTransitionNames() {
        return newSet(TRANS_ABILITY_DEMONSTRATED, TRANS_ABILITY_SKIPPED);
    }
}
