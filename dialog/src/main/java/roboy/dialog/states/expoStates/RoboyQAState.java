package roboy.dialog.states.expoStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.dialog.states.definitions.ExpoState;
import roboy.linguistics.Linguistics;
import roboy.linguistics.Triple;
import roboy.linguistics.sentenceanalysis.Interpretation;
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
 * 2) Outgoing transitions that have to be defined,
 *    following state if the question was answered:
 *    - skills,
 *    - abilities,
 *    - newPerson.
 * 3) Used 'infoFile' parameter containing Roboy answer phrases.
 *    Requires a path to RoboyInfoList.json
 */
public class RoboyQAState extends ExpoState {
    public final static String INTENTS_HISTORY_ID = "RQA";

    private final String[] TRANSITION_NAMES = { "skills", "abilities", "newPerson"};
    private final String[] INTENT_NAMES = TRANSITION_NAMES;

    private final String INFO_FILE_PARAMETER_ID = "infoFile";
    private final RandomList<String> connectingPhrases = PhraseCollection.CONNECTING_PHRASES;
    private final RandomList<String> roboyIntentPhrases = PhraseCollection.INFO_ROBOY_INTENT_PHRASES;

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
        Interlocutor person = getContext().ACTIVE_INTERLOCUTOR.getValue();
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
                    nextState = getTransitionRandomly(TRANSITION_NAMES, INTENT_NAMES, INTENTS_HISTORY_ID);
                    return Output.say(String.format(
                            infoValues.getSuccessAnswers(Neo4jRelationship.FRIEND_OF).getRandomElement(),
                            nodeName));
                }
            }
            nextState = getTransitionRandomly(TRANSITION_NAMES, INTENT_NAMES, INTENTS_HISTORY_ID);
            return Output.useFallback();
        }

        String answer = inferMemoryAnswer(input, roboy);
        nextState = getTransitionRandomly(TRANSITION_NAMES, INTENT_NAMES, INTENTS_HISTORY_ID);
        if (answer.equals("")) {
            return Output.useFallback();
        }
        return Output.say(answer);
    }

    private String inferMemoryAnswer(Interpretation input, Roboy roboy) {
        String answer = "";
        if (input.getSemTriples() != null) {
            List<Triple> triples = input.getSemTriples();
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
        } else if (objectAnswer.contains("where") ||
                objectAnswer.contains("city") ||
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
            if (node.getProperties().containsKey(full_name) && !node.getProperties().get(full_name).equals("")) {
                return node.getProperties().get(full_name).toString();
            } else {
                return node.getProperties().get(name).toString();
            }
        }
        return null;
    }

    @Override
    public State getNextState() {
        return nextState;
    }

    @Override
    protected Set<String> getRequiredTransitionNames() {
        return newSet(TRANSITION_NAMES);
    }
}
