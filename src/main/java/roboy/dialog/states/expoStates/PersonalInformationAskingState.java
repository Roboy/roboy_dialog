package roboy.dialog.states.expoStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.context.Context;
import roboy.context.contextObjects.IntentValue;
import roboy.dialog.Segue;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.Linguistics;
import roboy.linguistics.Triple;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.Neo4jProperty;
import roboy.memory.Neo4jRelationship;
import roboy.memory.nodes.Interlocutor;
import roboy.memory.nodes.Roboy;
import roboy.util.QAJsonParser;
import roboy.util.RandomList;

import java.util.*;

import static roboy.memory.Neo4jProperty.abilities;
import static roboy.memory.Neo4jProperty.skills;
import static roboy.memory.Neo4jRelationship.*;

/**
 * Personal Information Asking State
 *
 * The state tries to interact with the Interlocutor to learn new information about the person.
 * This information is sent to the Roboy Memory Module through Neo4jMemoryInterface for storing.
 * Afterwards, Roboy can use this acquired data for the future interactions with the same person.
 *
 * - if there is no existing Interlocutor or the data is missing, ask a question
 * - the question topic (intent) is selected from the Neo4jRelationship predicates
 * - retrieve the questions stored in the QAList json file
 * - update the Context IntentsHistory
 * - try to extract the result from the Interpretation
 * - retrieve the answers stored in the QAList json file
 * - send the result to Memory
 *
 * PersonalInformationAskingState interface:
 * 1) Fallback is not required.
 * 2) Outgoing transitions that have to be defined:
 *    - TRANSITION_INFO_OBTAINED:    following state if the question was asked
 * 3) Required parameters: path to the QAList.json file.
 */
public class PersonalInformationAskingState extends State {
    public final static String INTENTS_HISTORY_ID = "PIA";

    private QAJsonParser qaValues;
    private Neo4jRelationship[] predicates = { FROM, HAS_HOBBY, WORK_FOR, STUDY_AT };
    private Neo4jRelationship selectedPredicate;
    private State nextState;

    private final String SELECTED_SKILLS = "skills";
    private final String SELECTED_ABILITIES = "abilities";
    private final String SELECTED_ROBOY_QA = "roboy";

    private final String QA_FILE_PARAMETER_ID = "qaFile";
    final Logger LOGGER = LogManager.getLogger();

    public PersonalInformationAskingState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
        String qaListPath = params.getParameter(QA_FILE_PARAMETER_ID);
        LOGGER.info(" -> The QAList path: " + qaListPath);
        qaValues = new QAJsonParser(qaListPath);
    }

    @Override
    public Output act() {
        Interlocutor person = Context.getInstance().ACTIVE_INTERLOCUTOR.getValue();
        LOGGER.info(" -> Retrieved Interlocutor: " + person.getName());

        for (Neo4jRelationship predicate : predicates) {
            if (!person.hasRelationship(predicate)) {
                selectedPredicate = predicate;
                LOGGER.info(" -> Selected predicate: " + selectedPredicate.type);
                break;
            }
        }
        if (selectedPredicate == null) {
            selectedPredicate = OTHER;
        }
        RandomList<String> questions = qaValues.getQuestions(selectedPredicate);
        String question = "";
        if (questions != null && !questions.isEmpty()) {
            question = questions.getRandomElement();
            LOGGER.info(" -> Selected question: " + question);
        } else {
            LOGGER.error(" -> The list of " + selectedPredicate.type + " questions is empty or null");
        }
        try {
            Context.getInstance().DIALOG_INTENTS_UPDATER.updateValue(new IntentValue(INTENTS_HISTORY_ID, selectedPredicate));
            LOGGER.info(" -> Dialog IntentsHistory updated");
        } catch (Exception e) {
            LOGGER.error(" -> Error on updating the IntentHistory: " + e.getMessage());
        }
        return Output.say(question);

    }

    @Override
    public Output react(Interpretation input) {
        Interlocutor person = Context.getInstance().ACTIVE_INTERLOCUTOR.getValue();
        LOGGER.info("-> Retrieved Interlocutor: " + person.getName());
        RandomList<String> answers;
        String answer = "I have no words";
        String result = InferResult(input);
        if (selectedPredicate != null && selectedPredicate != OTHER) {
            if (result != null && !result.equals("")) {
                LOGGER.info(" -> Inference was successful");
                answers = qaValues.getSuccessAnswers(selectedPredicate);
                person.addInformation(selectedPredicate.type, result);
                Context.getInstance().ACTIVE_INTERLOCUTOR_UPDATER.updateValue(person);
                LOGGER.info(" -> Updated Interlocutor: " + person.getName());
            } else {
                LOGGER.warn(" -> Inference failed");
                answers = qaValues.getFailureAnswers(selectedPredicate);
                result = "";
                LOGGER.warn(" -> The result is empty. Nothing to store");
            }
        } else {
            answers = qaValues.getSuccessAnswers(OTHER);
        }

        if (answers != null && !answers.isEmpty()) {
            answer = String.format(answers.getRandomElement(), result);
        } else {
            LOGGER.error(" -> The list of " + selectedPredicate.type + " answers is empty or null");
        }

        LOGGER.info(" -> Produced answer: " + answer);
        nextState = getRandomTransition();
        Segue s = new Segue(Segue.SegueType.CONNECTING_PHRASE, 0.5);
        return Output.say(answer).setSegue(s);
    }

    @Override
    public State getNextState() {
        return nextState;
    }

    @Override
    protected Set<String> getRequiredTransitionNames() {
        // optional: define all required transitions here:
        return newSet(SELECTED_SKILLS, SELECTED_ABILITIES, SELECTED_ROBOY_QA);
    }

    @Override
    protected Set<String> getRequiredParameterNames() {
        return newSet(QA_FILE_PARAMETER_ID);
    }

    private String InferResult(Interpretation input) {
        String result = null;
        // TODO: What is the condition?
        if (input.getSentenceType().compareTo(Linguistics.SENTENCE_TYPE.STATEMENT) == 0) {
            String[] tokens = (String[]) input.getFeatures().get(Linguistics.TOKENS);
            if (tokens.length == 1) {
                result = tokens[0].replace("[", "").replace("]","").toLowerCase();
                LOGGER.info(" -> Retrieved only one token: " + result);
            } else {
                if (input.getFeatures().get(Linguistics.PARSER_RESULT).toString().equals("SUCCESS") &&
                        ((List<Triple>) input.getFeatures().get(Linguistics.SEM_TRIPLE)).size() != 0) {
                    List<Triple> sem_triple = (List<Triple>) input.getFeatures().get(Linguistics.SEM_TRIPLE);
                    LOGGER.info(" -> Semantic parsing is successful and semantic triple exists");
                    if (sem_triple.get(0).predicate.contains(selectedPredicate.type)) {
                        LOGGER.info(" -> Semantic predicate " + selectedPredicate.type + " exits");
                        result = sem_triple.get(0).object.toLowerCase();
                        LOGGER.info(" -> Retrieved object " + result);
                    } else {
                        LOGGER.warn(" -> Semantic predicate " + selectedPredicate.type + " does not exit");
                    }
                } else {
                    LOGGER.warn(" -> Semantic parsing failed or semantic triple does not exist");
                    if (input.getFeatures().get(Linguistics.OBJ_ANSWER) != null) {
                        LOGGER.info(" -> OBJ_ANSWER exits");
                        result = input.getFeatures().get(Linguistics.OBJ_ANSWER).toString().toLowerCase();
                        if (!result.equals("")) {
                            LOGGER.info(" -> Retrieved OBJ_ANSWER result " + result);
                        } else {
                            LOGGER.warn(" -> OBJ_ANSWER result is empty");
                        }
                    } else {
                        LOGGER.warn(" -> OBJ_ANSWER does not exit");
                    }
                }
            }
        } else {
            LOGGER.warn(" -> The sentence type is NOT a STATEMENT");
        }
        return result;
    }

    private State getRandomTransition() {
        int dice = (int) (4 * Math.random() + 1);
        switch (dice) {
            case 1:
                String skill = chooseIntentAttribute(skills);
                if (!skill.equals("")) {
                    Context.getInstance().DIALOG_INTENTS_UPDATER.updateValue(new IntentValue(INTENTS_HISTORY_ID, skills, skill));
                    return getTransition(SELECTED_SKILLS);
                } else {
                    return this;
                }
            case 2:
                String ability = chooseIntentAttribute(abilities);
                if (!ability.equals("")) {
                    Context.getInstance().DIALOG_INTENTS_UPDATER.updateValue(new IntentValue(INTENTS_HISTORY_ID, abilities, ability));
                    return getTransition(SELECTED_ABILITIES);
                } else {
                    return this;
                }
            case 3:
                return getTransition(SELECTED_ROBOY_QA);
            default:
                return this;
        }
    }

    private String chooseIntentAttribute(Neo4jProperty predicate) {
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
        return attribute;
    }

    private boolean lastNIntentsContainAttribute(String attribute, int n) {
        Map<Integer, IntentValue> lastIntentValues = Context.getInstance().DIALOG_INTENTS.getLastNValues(n);

        for (IntentValue value : lastIntentValues.values()) {
            if (value.getAttribute().equals(attribute)) {
                return true;
            }
        }
        return false;
    }
}
