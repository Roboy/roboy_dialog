package roboy.dialog.states.expoStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.context.Context;
import roboy.context.contextObjects.IntentValue;
import roboy.dialog.states.definitions.ExpoState;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.Neo4jProperty;
import roboy.memory.nodes.Interlocutor;
import roboy.memory.nodes.Roboy;
import roboy.util.Agedater;
import roboy.util.QAJsonParser;
import roboy.util.RandomList;

import java.util.*;

import static roboy.memory.Neo4jProperty.*;


/**
 * Expo Introduction State
 *
 * This state will:
 * - ask the interlocutor for his name
 * - create and update the interlocutor in the context
 * - take one transition: roboyInfo
 *
 * ExpoIntroductionState interface:
 * 1) Fallback is not required.
 * 2) Outgoing transitions that have to be defined,
 *    following state if Roboy introduced himself:
 *    - skills,
 *    - roboy,
 *    - abilities,
 *    - newPerson.
 * 3) Used 'infoFile' parameter containing Roboy answer phrases.
 *    Requires a path to RoboyInfoList.json
 */
public class ExpoIntroductionState extends ExpoState {
    public final static String INTENTS_HISTORY_ID = "RIS";

    private final String SELECTED_SKILLS = "skills";
    private final String SELECTED_ABILITIES = "abilities";
    private final String SELECTED_ROBOY_QA = "roboy";
    private final String LEARN_ABOUT_PERSON = "newPerson";
    private final String INFO_FILE_PARAMETER_ID = "infoFile";

    private final Logger LOGGER = LogManager.getLogger();

    private final RandomList<String> introPhrases = new RandomList<>(
            "Oh wow.. Sorry for my confusion today. But what's your name?",
            "Mamma mia. So many people passing by today. Good you stopped by to talk to me. Could you tell me your name?",
            "Ehm, sorry.. Who am I currently talking to? These lights are so bright I cant see your face"
            );
    private final RandomList<String> responsePhrases = new RandomList<>("Nice to meet you, %s!", "I am glad to meet you, %s!");

    private QAJsonParser infoValues;
    private State nextState;

    public ExpoIntroductionState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
        String infoListPath = params.getParameter(INFO_FILE_PARAMETER_ID);
        LOGGER.info(" -> The infoList path: " + infoListPath);
        infoValues = new QAJsonParser(infoListPath);
    }

    @Override
    public Output act() {
        return Output.say(getIntroPhrase());
    }

    @Override
    public Output react(Interpretation input) {
        // 1. get name
        String name = getNameFromInput(input);

        if (name == null) {
            nextState = this;
            LOGGER.warn("IntroductionState couldn't get name! Staying in the same state.");
            return Output.say("Sorry, my parser is out of service.");
        }

        // 2. get interlocutor object from context
        // this also should query memory and do other magic
        Interlocutor person = getContext().ACTIVE_INTERLOCUTOR.getValue();
        person.addName(name);

        // 3. update interlocutor in context
        updateInterlocutorInContext(person);

        Roboy roboy = new Roboy(getMemory());

        return Output.say(getResponsePhrase(name) + getRoboyFactsPhrase(roboy));
    }

    private String getNameFromInput(Interpretation input) {
        return getInference().inferProperty(name, input);
    }

    @Override
    public State getNextState() {
        return nextState;
    }

    private String getRoboyFactsPhrase(Roboy roboy) {
        String result = "";
        String retrievedRandomSkill = "";
        String retrievedRandomAbility = "";

        // Get some random properties facts
        if (roboy.getProperties() != null && !roboy.getProperties().isEmpty()) {
            HashMap<Neo4jProperty, Object> properties = roboy.getProperties();
            if (properties.containsKey(full_name)) {
                result += " " + String.format(infoValues.getSuccessAnswers(full_name).getRandomElement(), properties.get(full_name));
            }
            if (properties.containsKey(birthdate)) {
                HashMap<String, Integer> ages = new Agedater().determineAge(properties.get(birthdate).toString());
                String retrievedAge = "0 days";
                if (ages.get("years") > 0) {
                    retrievedAge = ages.get("years") + " years";
                } else if (ages.get("months") > 0) {
                    retrievedAge = ages.get("months") + " months";
                } else {
                    retrievedAge = ages.get("days") + " days";
                }
                result += " " + String.format(infoValues.getSuccessAnswers(age).getRandomElement(), retrievedAge);
            } else if (properties.containsKey(age)) {
                result += " " + String.format(infoValues.getSuccessAnswers(age).getRandomElement(), properties.get(age) + " years!");
            }
            if (properties.containsKey(skills)) {
                RandomList<String> retrievedResult = new RandomList<>(Arrays.asList(properties.get(skills).toString().split(",")));
                retrievedRandomSkill = retrievedResult.getRandomElement();
                result += " " + String.format(infoValues.getSuccessAnswers(skills).getRandomElement(), retrievedRandomSkill);
            }
            if (properties.containsKey(abilities)) {
                RandomList<String> retrievedResult = new RandomList<>(Arrays.asList(properties.get(abilities).toString().split(",")));
                retrievedRandomAbility = retrievedResult.getRandomElement();
                result += " " + String.format(infoValues.getSuccessAnswers(abilities).getRandomElement(), retrievedRandomAbility);
            }
            if (properties.containsKey(future)) {
                RandomList<String> retrievedResult = new RandomList<>(Arrays.asList(properties.get(future).toString().split(",")));
                result += " " + String.format(infoValues.getSuccessAnswers(future).getRandomElement(), retrievedResult.getRandomElement()) + " ";
            }
        }

        if (result.equals("")) {
            result = " I am Roboy 2.0! ";
        }

        nextState = getTransitionRandomly(retrievedRandomSkill, retrievedRandomAbility);

        return result;
    }

    private String getIntroPhrase() {
        return introPhrases.getRandomElement();
    }

    private String getResponsePhrase(String name) {
        return String.format(responsePhrases.getRandomElement(), name);
    }

    private State getTransitionRandomly(String skill, String ability) {
        int dice = (int) (3 * Math.random() + 1);
        switch (dice) {
            case 1:
                getContext().DIALOG_INTENTS_UPDATER.updateValue(new IntentValue(INTENTS_HISTORY_ID, skills, skill));
                return getTransition(SELECTED_SKILLS);
            case 2:
                getContext().DIALOG_INTENTS_UPDATER.updateValue(new IntentValue(INTENTS_HISTORY_ID, abilities, ability));
                return getTransition(SELECTED_ABILITIES);
            case 3:
                return getTransition(SELECTED_ROBOY_QA);
            default:
                return getTransition(LEARN_ABOUT_PERSON);
        }
    }

    private void updateInterlocutorInContext(Interlocutor interlocutor) {
        getContext().ACTIVE_INTERLOCUTOR_UPDATER.updateValue(interlocutor);
    }
}
