package roboy.dialog.states.expoStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.context.Context;
import roboy.context.contextObjects.IntentValue;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.Linguistics;
import roboy.linguistics.Triple;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.Neo4jProperty;
import roboy.memory.Neo4jRelationship;
import roboy.memory.nodes.Interlocutor;
import roboy.memory.nodes.MemoryNodeModel;
import roboy.memory.nodes.Roboy;
import roboy.talk.PhraseCollection;
import roboy.util.QAJsonParser;
import roboy.util.RandomList;

import java.util.*;

import static roboy.memory.Neo4jProperty.*;

/**
 * Roboy Question Answering State
 *
 * This state will:
 * - offer the interlocutor to ask a question about Roboy
 * - retrive the semantic parser result
 * - try to infer an asked question
 * - retrive the relevant information from the Roboy node
 * - compose an answer
 * - fall back in case of failure
 *
 * ExpoIntroductionState interface:
 * 1) Fallback is required.
 * 2) Outgoing transitions that have to be defined:
 *    - skills:    following state if the question was answered
 *    - abilities: following state if the question was answered
 *    - newPerson: following state if the question was answered
 * 3) Used 'infoFile' parameter containing Roboy answer phrases.
 *    Requires a path to RoboyInfoList.json
 */
public class RoboyQAState extends State {
    public final static String INTENTS_HISTORY_ID = "RQA";

    private final String SELECTED_SKILLS = "skills";
    private final String SELECTED_ABILITIES = "abilities";
    private final String LEARN_ABOUT_PERSON = "newPerson";

    private final RandomList<String> connectingPhrases = PhraseCollection.CONNECTING_PHRASES;
    private final RandomList<String> roboyIntentPhrases = PhraseCollection.INFO_ROBOY_INTENT_PHRASES;
    private final String INFO_FILE_PARAMETER_ID = "infoFile";

    private final Logger LOGGER = LogManager.getLogger();

    private QAJsonParser infoValues;
    private State nextState;
    private boolean intentIsFriend = false;

    public RoboyQAState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
        String infoListPath = params.getParameter(INFO_FILE_PARAMETER_ID);
        LOGGER.info(" -> The infoList path: " + infoListPath);
        infoValues = new QAJsonParser(infoListPath);
    }

    @Override
    public Output act() {
        Interlocutor person = Context.getInstance().ACTIVE_INTERLOCUTOR.getValue();
        String intentPhrase = roboyIntentPhrases.getRandomElement();
        intentIsFriend = intentPhrase.contains("friend");
        return Output.say(String.format(connectingPhrases.getRandomElement(), person.getName()) + intentPhrase);
    }

    @Override
    public Output react(Interpretation input) {
        Roboy roboy = new Roboy(getMemory());

        if (intentIsFriend) {
            Linguistics.UtteranceSentiment sentiment = getInference().inferSentiment(input);
            if (sentiment == Linguistics.UtteranceSentiment.POSITIVE) {
                String nodeName = extractNodeNameForPredicate(Neo4jRelationship.FRIEND_OF, roboy);
                if (nodeName != null) {
                    nextState = getRandomTransition();
                    return Output.say(String.format(
                            infoValues.getSuccessAnswers(Neo4jRelationship.FRIEND_OF).getRandomElement(),
                            nodeName));
                }
            }
            nextState = getRandomTransition();
            return Output.useFallback();
        }

        String answer = inferMemoryAnswer(input, roboy);
        nextState = getRandomTransition();
        if (answer.equals("")) {
            return Output.useFallback();
        }
        return Output.say(answer);
    }

    private String inferMemoryAnswer(Interpretation input, Roboy roboy) {
        String answer = "";
        if (input.semParserTriples != null) {
            List<Triple> triples = input.semParserTriples;
            for (Triple result : triples) {
                if (result.predicate != null) {
                    for (Neo4jRelationship predicate : Roboy.VALID_NEO4J_RELATIONSHIPS) {
                        if (result.predicate.contains(predicate.type)) {
                            String nodeName = extractNodeNameForPredicate(predicate, roboy);
                            if (nodeName != null) {
                                answer = String.format(infoValues.getSuccessAnswers(predicate).getRandomElement(), nodeName);
                                break;
                            }
                        }
                    }
                    if (!answer.equals("")){
                        break;
                    }
                }
            }
        } else if (input.getObjAnswer() != null) {
            String objAnswer = input.getObjAnswer().toLowerCase();
            if (!objAnswer.equals("")) {
                LOGGER.info("OBJ_ANSWER: " + objAnswer);
                Neo4jRelationship predicate = inferPredicateFromObjectAnswer(objAnswer);
                if (predicate != null) {
                    String nodeName = extractNodeNameForPredicate(predicate, roboy);
                    if (nodeName != null) {
                        answer = String.format(infoValues.getSuccessAnswers(predicate).getRandomElement(), nodeName);
                    }
                }
            } else {
                LOGGER.warn("OBJ_ANSWER is empty");
            }
        }
        return answer;
    }

    private Neo4jRelationship inferPredicateFromObjectAnswer(String objectAnswer) {
        if (objectAnswer.contains("hobb")) {
            return Neo4jRelationship.HAS_HOBBY;
        } else if (objectAnswer.contains("member")) {
            return Neo4jRelationship.MEMBER_OF;
        } else if (objectAnswer.contains("friend")) {
            return Neo4jRelationship.FRIEND_OF;
        } else if (objectAnswer.contains("city") ||
                objectAnswer.contains("place") ||
                objectAnswer.contains("country") ||
                objectAnswer.contains("live") ||
                objectAnswer.contains("life")) {
            return Neo4jRelationship.LIVE_IN;
        } else if (objectAnswer.contains("from") ||
                objectAnswer.contains("born")) {
            return Neo4jRelationship.FROM;
        } else if (objectAnswer.contains("child") ||
                objectAnswer.contains("father") ||
                objectAnswer.contains("dad") ||
                objectAnswer.contains("parent")) {
            return Neo4jRelationship.CHILD_OF;
        } else if (objectAnswer.contains("brother") ||
                objectAnswer.contains("family") ||
                objectAnswer.contains("relativ")) {
            return Neo4jRelationship.SIBLING_OF;
        }
        return null;
    }

    private String extractNodeNameForPredicate(Neo4jRelationship predicate, Roboy roboy) {
        MemoryNodeModel node = getMemNodesByIds(roboy.getRelationships(predicate)).getRandomElement();
        if (node != null) {
            if (node.getProperties().containsKey(full_name.type) && !node.getProperties().get(full_name.type).equals("")) {
                return node.getProperties().get(full_name.type).toString();
            } else {
                return node.getProperties().get(name.type).toString();
            }
        }
        return null;
    }

    @Override
    public State getNextState() {
        return nextState;
    }

    private State getRandomTransition() {
        int dice = (int) (3 * Math.random() + 1);
        switch (dice) {
            case 1:
                String skill = chooseIntentAttribute(skills);
                if (!skill.equals("")) {
                    Context.getInstance().DIALOG_INTENTS_UPDATER.updateValue(new IntentValue(INTENTS_HISTORY_ID, skills, skill));
                    LOGGER.info("SELECTED_SKILLS transition");
                    return getTransition(SELECTED_SKILLS);
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
                LOGGER.info("Stay in the current state");
                return this;
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

    @Override
    protected Set<String> getRequiredTransitionNames() {
        return newSet(SELECTED_SKILLS, SELECTED_ABILITIES, LEARN_ABOUT_PERSON);
    }
}
