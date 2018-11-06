package roboy.dialog.states.botboy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.context.contextObjects.IntentValue;
import roboy.dialog.Segue;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.Neo4jRelationship;
import roboy.memory.nodes.Interlocutor;
import roboy.memory.nodes.MemoryNodeModel;
import roboy.util.QAJsonParser;
import roboy.util.RandomList;

import java.util.Set;

import static roboy.memory.Neo4jRelationship.*;
import static roboy.memory.Neo4jProperty.*;

/**
 * Personal Information Update State
 *
 * This state is only entered if there are some known facts about the active interlocutor.
 * The state tries to interact with the Interlocutor to update the existing information about the person.
 * This information is sent to the Roboy Memory Module through Neo4jMemoryInterface to keep it up to date.
 *
 * - if there is an existing entry under a specific Neo4jRelationship predicate, select the predicate
 * - check the Context IntentsHistory if we already asked similar questions
 * - the question topic (intent) is selected upon the predicate
 * - update the Context IntentsHistory with the selected predicate
 * - retrieve the follow-up questions stored in the QAList json file
 * - retrieve the follow-up answers stored in the QAList json file
 *
 * BotBoyPersonalInformationFollowUpState interface:
 * 1) Fallback is not required.
 * 2) Outgoing transitions that have to be defined:
 *    - TRANSITION_INFO_UPDATED:    following state if the question was asked
 * 3) Required parameters: path to the QAList.json file.
 */

public class BotBoyPersonalInformationFollowUpState extends State {
    private QAJsonParser qaValues;
    private Neo4jRelationship[] predicates = { FROM, HAS_HOBBY, WORK_FOR, STUDY_AT };
    private Neo4jRelationship selectedPredicate;
    private State nextState;

    private final String TRANSITION_INFO_UPDATED = "questionAnswering";
    private final String QA_FILE_PARAMETER_ID = "qaFile";
    private final Logger LOGGER = LogManager.getLogger();

    public final static String INTENTS_HISTORY_ID = "FUP";

    public BotBoyPersonalInformationFollowUpState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
        String qaListPath = params.getParameter(QA_FILE_PARAMETER_ID);
        LOGGER.info(" -> The QAList path: " + qaListPath);
        qaValues = new QAJsonParser(qaListPath);
    }

    @Override
    public Output act() {
        Interlocutor person = getContext().ACTIVE_INTERLOCUTOR.getValue();
        LOGGER.info("-> Retrieved Interlocutor: " + person.getName());

        for (Neo4jRelationship predicate : predicates) {
            if (person.hasRelationship(predicate) &&
                    !getContext().DIALOG_INTENTS.contains(new IntentValue(INTENTS_HISTORY_ID, predicate)) &&
                    !getContext().DIALOG_INTENTS.contains(new IntentValue(BotBoyPersonalInformationAskingState.INTENTS_HISTORY_ID, predicate))) {
                selectedPredicate = predicate;
                LOGGER.info(" -> Selected predicate: " + selectedPredicate.type);
                break;
            }
        }

        Segue s = new Segue(Segue.SegueType.DISTRACT, 1.0);
        if (selectedPredicate != null) {
            RandomList<String> questions = qaValues.getFollowUpQuestions(selectedPredicate);
            String retrievedResult = "";
            RandomList<MemoryNodeModel> nodes = getMemNodesByIds(person.getRelationships(selectedPredicate));
            if (!nodes.isEmpty()) {
                retrievedResult = nodes.getRandomElement().getProperties().get(name).toString();
                LOGGER.info(" -> Retrieved memory node name: " + retrievedResult);
            } else {
                LOGGER.error("Could not retrieve memory data");
            }
            if (!retrievedResult.equals("")) {
                String question = "";
                if (questions != null && !questions.isEmpty()) {
                    question = String.format(questions.getRandomElement(), retrievedResult);
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
                return Output.say(question);
            } else {
                LOGGER.error("The retrieved memory data is empty");
                return Output.sayNothing().setSegue(s);
            }
        } else {
            return Output.sayNothing().setSegue(s);
        }
    }

    @Override
    public Output react(Interpretation input) {
        Interlocutor person = getContext().ACTIVE_INTERLOCUTOR.getValue();
        LOGGER.info("-> Retrieved Interlocutor: " + person.getName());
        RandomList<String> answers;
        String answer = "";
        String result = InferUpdateResult(input);

        if (selectedPredicate != null) {
            if (result != null && !result.equals("")) {
                LOGGER.info(" -> Inference was successful");
                answers = qaValues.getFollowUpAnswers(selectedPredicate);
                person.addInformation(selectedPredicate, result);
                getContext().ACTIVE_INTERLOCUTOR_UPDATER.updateValue(person);
                LOGGER.info(" -> Updated Interlocutor: " + person.getName());
            } else {
                LOGGER.warn(" -> Inference failed");
                answers = qaValues.getFollowUpAnswers(selectedPredicate);
                LOGGER.warn(" -> The result is empty. Nothing to update");
            }
            if (answers != null && !answers.isEmpty()) {
                answer = String.format(answers.getRandomElement(), "");
            } else {
                LOGGER.error(" -> The list of " + selectedPredicate + " answers is empty or null");
            }
        } else {
            LOGGER.error(" -> Selected predicate is null");
        }

        nextState = getTransition(TRANSITION_INFO_UPDATED);
        Segue s = new Segue(Segue.SegueType.CONNECTING_PHRASE);

        if (answer.equals("")) {
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
        return newSet(TRANSITION_INFO_UPDATED);
    }

    @Override
    protected Set<String> getRequiredParameterNames() {
        return newSet(QA_FILE_PARAMETER_ID);
    }

    private String InferUpdateResult(Interpretation input) {
        String result = null;
        // TODO: Implement
        // TODO: Will need to consider proper conditions for processing

        return result;
    }
}
