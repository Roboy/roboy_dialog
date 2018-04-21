package roboy.dialog.states.expoStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.context.Context;
import roboy.context.contextObjects.IntentValue;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.Linguistics.UtteranceSentiment;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.nodes.Interlocutor;
import roboy.memory.nodes.Roboy;
import roboy.talk.PhraseCollection;
import roboy.util.QAJsonParser;
import roboy.util.RandomList;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

import static roboy.memory.Neo4jProperty.*;

public class RoboyInfoState extends State {
    public final static String INTENTS_HISTORY_ID = "RIS";

    private final String SELECTED_SKILLS = "skills";
    private final String SELECTED_ABILITIES = "abilities";
    private final String SELECTED_ROBOY_QA = "roboy";
    private final String LEARN_ABOUT_PERSON = "newPerson";
    private final String INFO_FILE_PARAMETER_ID = "infoFile";
    private final Logger LOGGER = LogManager.getLogger();


    private final RandomList<String> offerPhrases = new RandomList<>("Do you want me to %s?", "Would you like me to %s?", "Should I demonstrate you how I %s?");
    private final RandomList<String> connectingPhrases = PhraseCollection.SEGUE_CONNECTING_PHRASES;
    private final RandomList<String> positivePhrases = new RandomList<>(" lets do it!", " behold me!", " I will give you the show of your life!", " that is the best decision!");
    private final RandomList<String> negativePhrases = new RandomList<>(" let me ask you a question then!", " was I not polite enough?", " lets switch gears!", " lets change the topic!");
    private final RandomList<String> roboyQAPhrases = new RandomList<>(" answer some questions about myself", " tell you more about myself");

    private QAJsonParser infoValues;
    private State nextState;

    public RoboyInfoState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
        String infoListPath = params.getParameter(INFO_FILE_PARAMETER_ID);
        LOGGER.info(" -> The infoList path: " + infoListPath);
        infoValues = new QAJsonParser(infoListPath);
    }

    @Override
    public Output act() {
        return Output.say(getRoboyFactsPhrase(new Roboy(getMemory())));
    }

    @Override
    public Output react(Interpretation input) {
        Interlocutor person = Context.getInstance().ACTIVE_INTERLOCUTOR.getValue();

        UtteranceSentiment inputSentiment = getInference().inferSentiment(input);
        LOGGER.info("The detected sentiment is " + inputSentiment);
        if (inputSentiment.toBoolean == Boolean.TRUE) {
            nextState = getTransitionByIntent(Context.getInstance().DIALOG_INTENTS.getLastValue());
            return Output.say(getPositiveSentence(person.getName()));
        } else {
            nextState = getTransition(LEARN_ABOUT_PERSON);
            return Output.say(getNegativeSentence(person.getName()));
        }
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
            result = "I am Roboy 2.0! ";
        }

        result += getOfferSentence(chooseIntentAndStoreInContext(retrievedRandomSkill, retrievedRandomAbility));

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


    private String getConnectingPhrase(String name) {
        return String.format(connectingPhrases.getRandomElement(), name);
    }

    private String getOfferSentence(String roboyOfferPhrase) {
        return String.format(offerPhrases.getRandomElement(), roboyOfferPhrase);
    }

    private String getPositiveSentence(String name) {
        return getConnectingPhrase(name) + positivePhrases.getRandomElement();
    }

    private String getNegativeSentence(String name) {
        return getConnectingPhrase(name) + negativePhrases.getRandomElement();
    }

    private State getTransitionByIntent(IntentValue intentValue) {
        if (intentValue.getNeo4jPropertyValue() != null) {
            if (intentValue.getNeo4jPropertyValue() == skills) {
                return getTransition(SELECTED_SKILLS);
            } else if (intentValue.getNeo4jPropertyValue() == abilities) {
                return getTransition(SELECTED_ABILITIES);
            }
        } else if (intentValue.getStringValue() != null) {
            if (intentValue.getStringValue().equals("info")) {
                return getTransition(SELECTED_ROBOY_QA);
            }
        }
        return getTransition(LEARN_ABOUT_PERSON);
    }

    private String chooseIntentAndStoreInContext(String skill, String ability) {
        IntentValue propertyIntent;
        int dice = (int) (3 * Math.random() + 1);
        switch (dice) {
            case 1:
                propertyIntent = new IntentValue(INTENTS_HISTORY_ID, skills, skill);
                break;
            case 2:
                propertyIntent = new IntentValue(INTENTS_HISTORY_ID, abilities, ability);
                break;
            default:
                propertyIntent = new IntentValue(INTENTS_HISTORY_ID, "info", roboyQAPhrases.getRandomElement());
                break;
        }

        Context.getInstance().DIALOG_INTENTS_UPDATER.updateValue(propertyIntent);
        return propertyIntent.getAttribute();
    }
}
