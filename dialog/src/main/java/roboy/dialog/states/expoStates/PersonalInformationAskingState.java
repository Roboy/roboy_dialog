package roboy.dialog.states.expoStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.context.Context;
import roboy.context.contextObjects.IntentValue;
import roboy.dialog.Segue;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.dialog.states.definitions.ExpoState;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.Neo4jRelationship;
import roboy.memory.nodes.Interlocutor;
import roboy.util.QAJsonParser;
import roboy.util.RandomList;

import java.util.*;

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
 * 2) Outgoing transitions that have to be defined,
 *    following state if the question was asked:
 *    - skills,
 *    - abilities,
 *    - roboy.
 * 3) Required parameters: path to the QAList.json file.
 */
public class PersonalInformationAskingState extends ExpoState {
    public final static String INTENTS_HISTORY_ID = "PIA";

    private final String[] TRANSITION_NAMES = { "skills", "abilities", "roboy" };
    private final String[] INTENT_NAMES = TRANSITION_NAMES;

    private final String QA_FILE_PARAMETER_ID = "qaFile";

    private final Logger LOGGER = LogManager.getLogger();

    private QAJsonParser qaValues;
    private Neo4jRelationship[] predicates = { FROM, HAS_HOBBY, WORK_FOR, STUDY_AT };
    private Neo4jRelationship selectedPredicate;
    private int otherIndex;
    private State nextState;

    public PersonalInformationAskingState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
        String qaListPath = params.getParameter(QA_FILE_PARAMETER_ID);
        LOGGER.info(" -> The QAList path: " + qaListPath);
        qaValues = new QAJsonParser(qaListPath);
    }

    @Override
    public Output act() {
        Interlocutor person = getContext().ACTIVE_INTERLOCUTOR.getValue();
        LOGGER.info(" -> Retrieved Interlocutor: " + person.getName());

        for (Neo4jRelationship predicate : predicates) {
            IntentValue intentValue = new IntentValue(INTENTS_HISTORY_ID, predicate);
            if(!getContext().DIALOG_INTENTS.contains(intentValue)) {
                selectedPredicate = predicate;
                LOGGER.info(" -> Selected predicate: " + selectedPredicate.type);
                break;
            }
        }
        if (selectedPredicate == null || Math.random() < 0.5) {
            selectedPredicate = OTHER;
        }
        RandomList<String> questions = qaValues.getQuestions(selectedPredicate);
        String question = "";
        if (questions != null && !questions.isEmpty()) {
            if (selectedPredicate == OTHER) {
                do {
                    question = questions.getRandomElement();
                    otherIndex = questions.indexOf(question);
                }
                while (getContext().OTHER_Q.contains(otherIndex));
            } else {
                question = questions.getRandomElement();
            }
            LOGGER.info(" -> Selected question: " + question);
        } else {
            LOGGER.error(" -> The list of " + selectedPredicate.type + " questions is empty or null");
        }
        try {
            getContext().DIALOG_INTENTS_UPDATER.updateValue(new IntentValue(INTENTS_HISTORY_ID, selectedPredicate));
            getContext().OTHER_QUESTIONS_UPDATER.updateValue(otherIndex);
            LOGGER.info(" -> Dialog IntentsHistory updated");
        } catch (Exception e) {
            LOGGER.error(" -> Error on updating the IntentHistory: " + e.getMessage());
        }
        return Output.say(question);

    }

    @Override
    public Output react(Interpretation input) {
        Interlocutor person = getContext().ACTIVE_INTERLOCUTOR.getValue();
        LOGGER.info("-> Retrieved Interlocutor: " + person.getName());
        RandomList<String> answers;
        String answer = "I have no words";
        String result = InferResult(input);
        if (selectedPredicate != null && selectedPredicate != OTHER) {
            if (result != null && !result.equals("")) {
                LOGGER.info(" -> Inference was successful");
                answers = qaValues.getSuccessAnswers(selectedPredicate);
                person.addInformation(selectedPredicate, result);
                getContext().ACTIVE_INTERLOCUTOR_UPDATER.updateValue(person);
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
            if (selectedPredicate == OTHER) {
                answer = String.format(answers.get(otherIndex));
            } else {
                answer = String.format(answers.getRandomElement(), result);
            }
        } else {
            LOGGER.error(" -> The list of " + selectedPredicate + " answers is empty or null");
        }

        LOGGER.info(" -> Produced answer: " + answer);
        nextState = getTransitionRandomly(TRANSITION_NAMES, INTENT_NAMES, INTENTS_HISTORY_ID);
        Segue s = new Segue(Segue.SegueType.CONNECTING_PHRASE, 0.5);
        if (answer == "") {
            return Output.useFallback();
        }
        return Output.say(answer).setSegue(s);
    }

    @Override
    public State getNextState() {
        return nextState;
    }

    @Override
    protected Set<String> getRequiredTransitionNames() {
        // optional: define all required transitions here:
        return newSet(TRANSITION_NAMES);
    }

    @Override
    protected Set<String> getRequiredParameterNames() {
        return newSet(QA_FILE_PARAMETER_ID);
    }

    private String InferResult(Interpretation input) {
        return getInference().inferRelationship(selectedPredicate, input);
    }
}
