package roboy.dialog.states.expoStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.context.Context;
import roboy.context.contextObjects.IntentValue;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.nodes.Interlocutor;
import roboy.memory.nodes.Roboy;
import roboy.util.QAJsonParser;
import roboy.util.RandomList;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;

import static roboy.memory.Neo4jProperty.*;


/**
 * This state will:
 * - ask the interlocutor for his name
 * - create and update the interlocutor in the context
 * - take one transition: roboyInfo
 *
 * ExpoIntroductionState interface:
 * 1) Fallback is not required.
 * 2) Outgoing transitions that have to be defined:
 *    - roboyInfo:    following state if the name was given
 * 3) No parameters are used.
 */
public class ExpoIntroductionState extends State {
    public final static String INTENTS_HISTORY_ID = "RIS";

    private final String SELECTED_SKILLS = "skills";
    private final String SELECTED_ABILITIES = "abilities";
    private final String SELECTED_ROBOY_QA = "roboy";
    private final String LEARN_ABOUT_PERSON = "newPerson";
    private final String INFO_FILE_PARAMETER_ID = "infoFile";

    private final Logger LOGGER = LogManager.getLogger();

    private final RandomList<String> introPhrases = new RandomList<>("What's your name?", "Could you tell me your name?");
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
        Interlocutor person = Context.getInstance().ACTIVE_INTERLOCUTOR.getValue();
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
            HashMap<String, Object> properties = roboy.getProperties();
            if (properties.containsKey(full_name.type)) {
                result += " " + String.format(infoValues.getSuccessAnswers(full_name).getRandomElement(), properties.get(full_name.type));
            }
            if (properties.containsKey(birthdate.type)) {
                result += " " + String.format(infoValues.getSuccessAnswers(age).getRandomElement(), determineAge(properties.get(birthdate.type).toString()));
            } else if (properties.containsKey(age.type)) {
                result += " " + String.format(infoValues.getSuccessAnswers(age).getRandomElement(), properties.get(age.type) + " years!");
            }
            if (properties.containsKey(skills.type)) {
                RandomList<String> retrievedResult = new RandomList<>(Arrays.asList(properties.get("skills").toString().split(",")));
                retrievedRandomSkill = retrievedResult.getRandomElement();
                result += " " + String.format(infoValues.getSuccessAnswers(skills).getRandomElement(), retrievedRandomSkill);
            }
            if (properties.containsKey(abilities.type)) {
                RandomList<String> retrievedResult = new RandomList<>(Arrays.asList(properties.get("abilities").toString().split(",")));
                retrievedRandomAbility = retrievedResult.getRandomElement();
                result += " " + String.format(infoValues.getSuccessAnswers(abilities).getRandomElement(), retrievedRandomAbility);
            }
            if (properties.containsKey(future.type)) {
                RandomList<String> retrievedResult = new RandomList<>(Arrays.asList(properties.get("future").toString().split(",")));
                result += " " + String.format(infoValues.getSuccessAnswers(future).getRandomElement(), retrievedResult.getRandomElement()) + " ";
            }
        }

        if (result.equals("")) {
            result = " I am Roboy 2.0! ";
        }

        nextState = getRandomTransition(retrievedRandomSkill, retrievedRandomAbility);

        return result;
    }

    /**
     * A helper function to determine the age based on the birthdate
     *
     * Java 8 specific
     *
     * @param datestring
     * @return
     */
    private String determineAge(String datestring) {
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
        Date date = null;
        try {
            date = format.parse(datestring);
        } catch (ParseException e) {
            LOGGER.error("Error while parsing a date: " + datestring + ". " + e.getMessage());
        }
        if (date != null) {
            LocalDate birthdate = date.toInstant().atZone(ZoneId.of("Europe/Berlin")).toLocalDate();
            LOGGER.info("The birthdate is " + birthdate.toString());
            LocalDate now = LocalDate.now(ZoneId.of("Europe/Berlin"));
            Period age = Period.between(birthdate, now);
            int years = age.getYears();
            int months = age.getMonths();
            int days = age.getDays();
            LOGGER.info("The estimated age is: " + years + " years, or " + months + " months, or " + days + " days!");
            if (years > 0) {
                return years + " years";
            } else if (months > 0) {
                return months + " months";
            } else {
                return days + " days";
            }
        } else {
            return "0 years";
        }
    }

    private String getIntroPhrase() {
        return introPhrases.getRandomElement();
    }

    private String getResponsePhrase(String name) {
        return String.format(responsePhrases.getRandomElement(), name);
    }

    private State getRandomTransition(String skill, String ability) {
        int dice = (int) (3 * Math.random() + 1);
        switch (dice) {
            case 1:
                Context.getInstance().DIALOG_INTENTS_UPDATER.updateValue(new IntentValue(INTENTS_HISTORY_ID, skills, skill));
                return getTransition(SELECTED_SKILLS);
            case 2:
                Context.getInstance().DIALOG_INTENTS_UPDATER.updateValue(new IntentValue(INTENTS_HISTORY_ID, abilities, ability));
                return getTransition(SELECTED_ABILITIES);
            case 3:
                return getTransition(SELECTED_ROBOY_QA);
            default:
                return getTransition(LEARN_ABOUT_PERSON);
        }
    }

    private void updateInterlocutorInContext(Interlocutor interlocutor) {
        Context.getInstance().ACTIVE_INTERLOCUTOR_UPDATER.updateValue(interlocutor);
    }
}