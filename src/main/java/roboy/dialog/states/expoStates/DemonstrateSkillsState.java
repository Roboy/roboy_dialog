package roboy.dialog.states.expoStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.context.Context;
import roboy.context.contextObjects.IntentValue;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.Neo4jProperty;
import roboy.memory.nodes.Interlocutor;
import roboy.memory.nodes.Roboy;
import roboy.talk.PhraseCollection;
import roboy.util.RandomList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static roboy.memory.Neo4jProperty.abilities;
import static roboy.memory.Neo4jProperty.skills;

enum RoboySkillIntent {
    jokes("joke"),
    fun_facts("fact"),
    famous_entities("famous"),
    math("math");

    public String type;

    RoboySkillIntent(String type) {
        this.type=type;
    }

    private final Logger LOGGER = LogManager.getLogger();

    private final RandomList<String> connectingPhrases = PhraseCollection.CONNECTING_PHRASES;
    private final RandomList<String> negativePhrases = PhraseCollection.NEGATIVE_SENTIMENT_PHRASES;
    private final RandomList<String> offerJokes = PhraseCollection.OFFER_JOKES_PHRASES;
    private final RandomList<String> offerFacts = PhraseCollection.OFFER_FACTS_PHRASES;
    private final RandomList<String> offerFamousEntities = PhraseCollection.OFFER_FAMOUS_ENTITIES_PHRASES;
    private final RandomList<String> offerMath = PhraseCollection.OFFER_MATH_PHRASES;
    private final RandomList<String> jokesList = PhraseCollection.JOKES;
    private final RandomList<String> factsList = PhraseCollection.FACTS;
    private final RandomList<String> parserError = PhraseCollection.PARSER_ERROR;

    public String getRequestPhrase() {
        switch (this) {
            case jokes:
                return offerJokes.getRandomElement();
            case fun_facts:
                return offerFacts.getRandomElement();
            case math:
                return offerMath.getRandomElement();
            case famous_entities:
                return offerFamousEntities.getRandomElement();
        }
        throw new AssertionError("Unknown error on enum entry: " + this);
    }

    public String getResponsePhrase(Interpretation input, Linguistics.UtteranceSentiment sentiment, String interlocutorName) {
        switch (this) {
            case jokes:
                return getRandomJoke(sentiment, interlocutorName);
            case fun_facts:
                return getRandomFact(sentiment, interlocutorName);
            case math:
                return getAnswerFromSemanticParser(input, interlocutorName);
            case famous_entities:
                return getAnswerFromSemanticParser(input, interlocutorName);
        }
        throw new AssertionError("Unknown error on enum entry: " + this);
    }

    private String getRandomJoke(Linguistics.UtteranceSentiment sentiment, String name) {
        if (sentiment != Linguistics.UtteranceSentiment.POSITIVE) {
            return getNegativeSentence(name);
        }

        return jokesList.getRandomElement();
    }

    private String getRandomFact(Linguistics.UtteranceSentiment sentiment, String name) {
        if (sentiment != Linguistics.UtteranceSentiment.POSITIVE) {
            return getNegativeSentence(name);
        }

        return factsList.getRandomElement();
    }

    private String getAnswerFromSemanticParser(Interpretation input, String name) {
        // TODO Semantic parser lightweight
        Linguistics.PARSER_OUTCOME parserOutcome = input.parserOutcome;
        if (parserOutcome == Linguistics.PARSER_OUTCOME.SUCCESS) {
            if (input.answer != null) {
                String result = input.answer;
                LOGGER.info("Parsing was successful! The result is " + result);
                return String.format(connectingPhrases.getRandomElement(), name) + result;
            } else {
                LOGGER.error("Parsing failed! Answer is null!");
            }
        }
        LOGGER.error("Parsing failed! Invalid parser outcome!");
        return parserError.getRandomElement();
    }

    private String getNegativeSentence(String name) {
        return String.format(connectingPhrases.getRandomElement(), name) + negativePhrases.getRandomElement();
    }
}

public class DemonstrateSkillsState extends State {
    public final static String INTENTS_HISTORY_ID = "RSS";

    private final String SELECTED_ABILITIES = "abilities";
    private final String SELECTED_ROBOY_QA = "roboy";
    private final String LEARN_ABOUT_PERSON = "newPerson";

    private final Logger LOGGER = LogManager.getLogger();

    private State nextState;
    private RoboySkillIntent skillIntent = null;

    public DemonstrateSkillsState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

    @Override
    public Output act() {
        IntentValue intentValue = Context.getInstance().DIALOG_INTENTS.getLastValue();
        if (intentValue.getNeo4jPropertyValue() == Neo4jProperty.skills) {
            LOGGER.info("Extracted intent value: [" +
                    intentValue.getId() + " / " +
                    intentValue.getNeo4jPropertyValue() + " / " +
                    intentValue.getAttribute() + "]");
            skillIntent = detectSkill(intentValue.getAttribute());
            if (skillIntent != null) {
                LOGGER.info("Extracted the following skill: " + skillIntent);
                return Output.say(skillIntent.getRequestPhrase());
            } else {
                LOGGER.error("Skills extraction failed");
            }
        } else {
            LOGGER.error("Unexpected intent value: [" +
                    intentValue.getId() + " / " +
                    intentValue.getNeo4jPropertyValue() + " / " +
                    intentValue.getAttribute() + "]");
        }
        return Output.sayNothing();
    }

    @Override
    public Output react(Interpretation input) {
        Interlocutor person = Context.getInstance().ACTIVE_INTERLOCUTOR.getValue();

        Linguistics.UtteranceSentiment inputSentiment = getInference().inferSentiment(input);
        LOGGER.info("The detected sentiment is " + inputSentiment);

        nextState = getRandomTransition();
        return Output.say(skillIntent.getResponsePhrase(input, inputSentiment, person.getName()));
    }

    @Override
    public State getNextState() {
        return nextState;
    }

    private RoboySkillIntent detectSkill(String attribute) {
        for (RoboySkillIntent intent : RoboySkillIntent.values()) {
            if (attribute.contains(intent.type)) {
                return intent;
            }
        }
        return null;
    }

    private State getRandomTransition() {
        LOGGER.info("Try to choose a random transition");
        int dice = (int) (3 * Math.random() + 1);
        switch (dice) {
            case 1:
                String skill = chooseIntentAttribute(skills);
                if (!skill.equals("")) {
                    Context.getInstance().DIALOG_INTENTS_UPDATER.updateValue(new IntentValue(INTENTS_HISTORY_ID, skills, skill));
                    LOGGER.info("Stay in the current state");
                    return this;
                } else {
                    LOGGER.info("LEARN_ABOUT_PERSON transition");
                    return getTransition(LEARN_ABOUT_PERSON);
                }
            case 2:
                String ability = chooseIntentAttribute(abilities);
                if (!ability.equals("")) {
                    Context.getInstance().DIALOG_INTENTS_UPDATER.updateValue(new IntentValue(INTENTS_HISTORY_ID, abilities, ability));
                    LOGGER.info("SELECTED_ABILITIES transition");
                    return getTransition(SELECTED_ABILITIES);
                } else {
                    LOGGER.info("LEARN_ABOUT_PERSON transition");
                    return getTransition(LEARN_ABOUT_PERSON);
                }
            case 3:
                LOGGER.info("SELECTED_ROBOY_QA transition");
                return getTransition(SELECTED_ROBOY_QA);
            default:
                LOGGER.info("LEARN_ABOUT_PERSON transition");
                return getTransition(LEARN_ABOUT_PERSON);
        }
    }

    private String chooseIntentAttribute(Neo4jProperty predicate) {
        LOGGER.info("Trying to choose the intent attribute");
        Roboy roboy = new Roboy(getMemory());
        String attribute = "";
        HashMap<String, Object> properties = roboy.getProperties();
        if (roboy.getProperties() != null && !roboy.getProperties().isEmpty()) {
            if (properties.containsKey(predicate.type)) {
                RandomList<String> retrievedResult = new RandomList<>(Arrays.asList(properties.get(predicate.type).toString().split(",")));
                int count = 0;
                do {
                    attribute = retrievedResult.getRandomElement();
                    count++;
                } while (lastNIntentsContainAttribute(attribute, 2) && count < retrievedResult.size());
            }
        }
        LOGGER.info("The chosen attribute: " + attribute);
        return attribute;
    }

    private boolean lastNIntentsContainAttribute(String attribute, int n) {
        Map<Integer, IntentValue> lastIntentValues = Context.getInstance().DIALOG_INTENTS.getLastNValues(n);

        for (IntentValue value : lastIntentValues.values()) {
            if (value.getAttribute() != null) {
                if (value.getAttribute().equals(attribute)) {
                    return true;
                }
            }
        }
        return false;
    }
}
