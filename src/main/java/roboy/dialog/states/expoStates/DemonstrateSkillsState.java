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
import roboy.talk.PhraseCollection;
import roboy.util.RandomList;

enum RoboySkills {
    jokes,
    fun_facts,
    famous_entities,
    math;

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

    public String getResponsePhrase(Interpretation input) {
        switch (this) {
            case jokes:
                return jokesList.getRandomElement();
            case fun_facts:
                return factsList.getRandomElement();
            case math:
                return getAnswerFromSemanticParser(input);
            case famous_entities:
                return getAnswerFromSemanticParser(input);
        }
        throw new AssertionError("Unknown error on enum entry: " + this);
    }

    private String getAnswerFromSemanticParser(Interpretation input) {
        // TODO Semantic parser
        if (true) {
            return null;
        } else {
            return parserError.getRandomElement();
        }
    }
}

public class DemonstrateSkillsState extends State {
    public final static String INTENTS_HISTORY_ID = "RIS";

    private final String SELECTED_ABILITIES = "abilities";
    private final String SELECTED_ROBOY_QA = "roboy";
    private final String LEARN_ABOUT_PERSON = "newPerson";

    private final Logger LOGGER = LogManager.getLogger();

    private final RandomList<String> connectingPhrases = PhraseCollection.CONNECTING_PHRASES;
    // private final RandomList<String> positivePhrases = new RandomList<>(" lets do it!", " behold me!", " I will give you the show of your life!", " that is the best decision!");
    private final RandomList<String> negativePhrases = new RandomList<>(" let me ask you a question then!", " was I not polite enough?", " lets switch gears!", " lets change the topic!");
    private final RandomList<String> roboyQAPhrases = new RandomList<>(" answer some questions about myself", " tell you more about myself");

    private State nextState;
    private RoboySkills roboySkill = null;

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
            roboySkill = detectSkill(intentValue.getAttribute());
            if (roboySkill != null) {
                LOGGER.info("Extracted the following subintent: " + roboySkill);
                return Output.say(roboySkill.getRequestPhrase());
            } else {
                LOGGER.info("Subintent extraction failed");
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
        if (inputSentiment.toBoolean == Boolean.TRUE) {
            String answer = "";
            if (roboySkill != null) {
                answer = roboySkill.getResponsePhrase(input);
            }
            nextState = getRandomTransition();
            return Output.say(getConnectingPhrase(person.getName()) + answer);
        } else {
            nextState = getRandomTransition();
            return Output.say(getNegativeSentence(person.getName()));
        }
    }

    @Override
    public State getNextState() {
        return nextState;
    }

    private String getConnectingPhrase(String name) {
        return String.format(connectingPhrases.getRandomElement(), name);
    }

    private String getNegativeSentence(String name) {
        return getConnectingPhrase(name) + negativePhrases.getRandomElement();
    }

    private RoboySkills detectSkill(String attribute) {
        for (RoboySkills intent : RoboySkills.values()) {
            if (attribute.contains(intent.toString())) {
                return intent;
            }
        }
        return null;
    }

    private State getRandomTransition() {
        int dice = (int) (4 * Math.random() + 1);
        switch (dice) {
            case 1:
                return this;
            case 2:
                return getTransition(SELECTED_ABILITIES);
            case 3:
                return getTransition(SELECTED_ROBOY_QA);
            default:
                return getTransition(LEARN_ABOUT_PERSON);
        }
    }
}
