package roboy.dialog.states.ordinaryStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.context.Context;
import roboy.context.contextObjects.IntentValue;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.Linguistics;
import roboy.linguistics.Triple;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.Neo4jRelationship;
import roboy.memory.nodes.Interlocutor;
import roboy.dialog.Segue;
import roboy.util.QAJsonParser;
import roboy.util.RandomList;

import java.util.List;
import java.util.Set;

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
    private QAJsonParser qaValues;
    private RandomList<Neo4jRelationship> predicates = new RandomList<>(FROM, HAS_HOBBY, WORK_FOR, STUDY_AT, LIVE_IN,
                                                FRIEND_OF, MEMBER_OF, WORK_FOR, OCCUPIED_AS);
    private Neo4jRelationship selectedPredicate;
    private State nextState;

    private final String TRANSITION_INFO_OBTAINED = "questionAnswering";
    private final String TRANSITION_FOLLOWUP = "followUp";
    private final String QA_FILE_PARAMETER_ID = "qaFile";
    final Logger LOGGER = LogManager.getLogger();

    public final static String INTENTS_HISTORY_ID = "PIA";

    // we have to track question's index of the predicate OTHER, since the answer's order matters
    private int otherIdx = 0;

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

        for (Neo4jRelationship predicate : predicates.shuffle()) {
            if (!person.hasRelationship(predicate) && !getContext().DIALOG_INTENTS.contains(new IntentValue(INTENTS_HISTORY_ID, predicate))) {
                selectedPredicate = predicate;
                getContext().DIALOG_INTENTS_UPDATER.updateValue(new IntentValue(INTENTS_HISTORY_ID, predicate));
                LOGGER.info(" -> Selected predicate: " + selectedPredicate.type);
                break;
            }
        }
        String question = "";
        if (selectedPredicate != null) {

            RandomList<String> questions = qaValues.getQuestions(selectedPredicate);

            if (questions != null && !questions.isEmpty()) {
                question = questions.getRandomElement();
                if (selectedPredicate.equals(OTHER))
                    otherIdx = questions.indexOf(question);
                LOGGER.info(" -> Selected question: " + question);
            } else {
                LOGGER.error(" -> The list of " + selectedPredicate.type + " questions is empty or null");
            }
            try {
                getContext().DIALOG_INTENTS_UPDATER.updateValue(new IntentValue(INTENTS_HISTORY_ID, selectedPredicate));
                LOGGER.info(" -> Dialog IntentsHistory updated");
            } catch (Exception e) {
                LOGGER.error(" -> Error on updating the IntentHistory: " + e.getMessage());
            }
            return State.Output.say(question);
        }
        else {
            RandomList<String> questions = qaValues.getQuestions(OTHER);
            selectedPredicate = OTHER;
            int idx;
            do {
                question = questions.getRandomElement();
                idx = questions.indexOf(question);
            } while (getContext().OTHER_Q.contains(idx));
            getContext().OTHER_QUESTIONS_UPDATER.updateValue(idx);
            return State.Output.say(question);
        }
    }

    @Override
    public Output react(Interpretation input) {
        Interlocutor person = getContext().ACTIVE_INTERLOCUTOR.getValue();
        LOGGER.info("-> Retrieved Interlocutor: " + person.getName());
        RandomList<String> answers = new RandomList<>();
        String answer = "";
        String result = InferResult(input);

        if(selectedPredicate != null) {

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
        }
        if (answers != null && !answers.isEmpty()) {
            if (selectedPredicate.equals(OTHER)) {
                answer = String.format(answers.get(getContext().OTHER_Q.getLastValue()), result);
            } else {
                answer = String.format(answers.getRandomElement(), result);
            }
        } else {
            LOGGER.error(" -> The list of " + selectedPredicate + " answers is empty or null");
            nextState = getTransition(TRANSITION_INFO_OBTAINED);
            return Output.useFallback();
        }
        LOGGER.info(" -> Produced answer: " + answer);
        nextState = getTransition(TRANSITION_FOLLOWUP);
        selectedPredicate = null;
//        Segue s = new Segue(Segue.SegueType.CONNECTING_PHRASE, 0.5);
        return answer.isEmpty() ? Output.useFallback() : Output.say(answer);
    }

    @Override
    public State getNextState() {
        return nextState;
    }

    @Override
    protected Set<String> getRequiredTransitionNames() {
        // optional: define all required transitions here:
        return newSet(TRANSITION_INFO_OBTAINED);
    }

    @Override
    protected Set<String> getRequiredParameterNames() {
        return newSet(QA_FILE_PARAMETER_ID);
    }

    private String InferResult(Interpretation input) {
        return getInference().inferRelationship(selectedPredicate, input);
    }
}
