package roboy.dialog.states.expoStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.context.Context;
import roboy.context.contextObjects.IntentValue;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.dialog.states.definitions.ExpoState;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.Neo4jProperty;
import roboy.memory.nodes.Interlocutor;
import roboy.ros.RosMainNode;
import roboy.talk.PhraseCollection;
import roboy.util.RandomList;

import java.util.Set;

/**
 * Enum implementation of Roboy's skills.
 *
 * General functionality:
 *  - getRequestPhrase()    -   provides a phrase to offer some skills activity
 *  - getResponsePhrase()   -   provides Roboy's response to the input
 *  - getNegativeSentence() -   provides response in case the intent was not POSITIVE
 *
 * Specific functionality:
 *  - getRandomJoke()               -   returns a string with a random joke
 *  - getRandomFact()               -   returns a string with a random fact
 *  - getAnswerFromSemanticParser() -   tries to resolve the question with the semantic parser,
 *                                      returns the resulting string on success,
 *                                      uses generative model on failure
 */
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

    public String getResponsePhrase(Interpretation input, Linguistics.UtteranceSentiment sentiment, String interlocutorName,
                                    RosMainNode rmn) {
        switch (this) {
            case jokes:
                return getRandomJoke(sentiment, interlocutorName);
            case fun_facts:
                return getRandomFact(sentiment, interlocutorName);
            case math:
                return getAnswerFromSemanticParser(input, interlocutorName, rmn);
            case famous_entities:
                return getAnswerFromSemanticParser(input, interlocutorName, rmn);
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

    private String getAnswerFromSemanticParser(Interpretation input, String name, RosMainNode rmn) {
        // TODO Semantic parser lightweight
        Linguistics.ParsingOutcome parserOutcome = input.getParsingOutcome();
        if (parserOutcome == Linguistics.ParsingOutcome.SUCCESS) {
            if (input.getAnswer() != null) {
                String result = input.getAnswer();
                LOGGER.info("Parsing was successful! The result is " + result);
                return String.format(connectingPhrases.getRandomElement(), name) + result;
            } else {
                LOGGER.error("Parsing failed! Answer is null!");
            }
        }

        LOGGER.error("Parsing failed! Invalid parser outcome!");
        String generativeAnswer = rmn.GenerateAnswer(input.getSentence());
        return generativeAnswer != null ? generativeAnswer : parserError.getRandomElement();
    }



    private String getNegativeSentence(String name) {
        return String.format(connectingPhrases.getRandomElement(), name) + negativePhrases.getRandomElement();
    }
}

/**
 * Roboy Demonstrate Skills State
 *
 * This state will:
 * - offer the interlocutor to ask a general question, mathematical problem
 * - retrive the semantic parser result
 * - compose an answer
 * - fall back in case of failure
 * OR
 * - offer a joke / an amusing fact
 * - in case of POSITIVE sentiment say those
 *
 * ExpoIntroductionState interface:
 * 1) Fallback is required.
 * 2) Outgoing transitions that have to be defined,
 *    following state if the question was answered or the joke/fact were told:
 *    - roboy,
 *    - abilities,
 *    - newPerson.
 * 3) No parameters are used.
 */
public class DemonstrateSkillsState extends ExpoState {
    public final static String INTENTS_HISTORY_ID = "RSS";

    private final String[] TRANSITION_NAMES = { "abilities", "roboy", "newPerson" };
    private final String[] INTENT_NAMES = TRANSITION_NAMES;

    private final Logger LOGGER = LogManager.getLogger();

    private State nextState;
    private RoboySkillIntent skillIntent = null;

    public DemonstrateSkillsState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

    @Override
    public Output act() {
        IntentValue intentValue = getContext().DIALOG_INTENTS.getLastValue();
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
        Interlocutor person = getContext().ACTIVE_INTERLOCUTOR.getValue();

        Linguistics.UtteranceSentiment inputSentiment = getInference().inferSentiment(input);
        LOGGER.info("The detected sentiment is " + inputSentiment);

        nextState = getTransitionRandomly(TRANSITION_NAMES, INTENT_NAMES, INTENTS_HISTORY_ID);
        String output = skillIntent.getResponsePhrase(input, inputSentiment, person.getName(), getRosMainNode());
        return output != null && !output.equals("") ? Output.say(output) : Output.useFallback();
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

    @Override
    protected Set<String> getRequiredTransitionNames() {
        return newSet(TRANSITION_NAMES);
    }
}
