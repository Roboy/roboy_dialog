package roboy.dialog.states.multiPartyStates;

import roboy.dialog.states.definitions.State;
import roboy.linguistics.sentenceanalysis.Interpretation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.context.Context;
import roboy.context.contextObjects.IntentValue;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.Linguistics;
import roboy.linguistics.Triple;
import roboy.memory.Neo4jRelationship;
import roboy.memory.nodes.Interlocutor;
import roboy.dialog.Segue;
import roboy.util.QAJsonParser;
import roboy.util.RandomList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static roboy.memory.Neo4jRelationship.*;

public abstract class PersonalInformationAskingStateParty extends State {

    private QAJsonParser qaValues;
    private Neo4jRelationship[] predicates = { FROM, HAS_HOBBY, WORK_FOR, STUDY_AT };
    private Neo4jRelationship selectedPredicate;
    private State nextState;

    private final String TRANSITION_INFO_OBTAINED = "questionAnswering";
    private final String QA_FILE_PARAMETER_ID = "qaFile";
    final Logger LOGGER = LogManager.getLogger();

    public final static String INTENTS_HISTORY_ID = "PIA";

    private int speakerCount;

    /**
     * Create a state object with given identifier (state name) and parameters.
     * <p>
     * The parameters should contain a reference to a state machine for later use.
     * The state will not automatically add itself to the state machine.
     *
     * @param stateIdentifier identifier (name) of this state
     * @param params          parameters for this state, should contain a reference to a state machine
     */
    public PersonalInformationAskingStateParty(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
        String qaListPath = params.getParameter(QA_FILE_PARAMETER_ID);
        LOGGER.info(" -> The QAList path: " + qaListPath);
        qaValues = new QAJsonParser(qaListPath);
        speakerCount = 1;
    }

    @Override
    public Output act() {

        /*
        Map<Integer, Interlocutor> persons = getContext().ACTIVE_INTERLOCUTORS.getValue();
        if(speakerCount>1) {
            Interlocutor second = persons.get(2);
            if(second != null){
                //new person detected
                Interlocutor person = new Interlocutor(getMemory());
                persons.put(2, person);
                getContext().ACTIVE_INTERLOCUTORS_UPDATER.updateValue(persons);
                //TODO introduction new speaker
            }
        }

        for(int i=0; i<speakerCount; i++){
            LOGGER.info("-> Retrieved Interlocutors: " + persons.get(i).getName());

            for (Neo4jRelationship predicate : predicates) {
                if (!persons.get(0).hasRelationship(predicate)) {
                    selectedPredicate = predicate;
                    LOGGER.info(" -> Selected predicate: " + selectedPredicate.type);
                    break;
                }
            }


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
            getContext().DIALOG_INTENTS_UPDATER.updateValue(new IntentValue(INTENTS_HISTORY_ID, selectedPredicate));
            LOGGER.info(" -> Dialog IntentsHistory updated");
        } catch (Exception e) {
            LOGGER.error(" -> Error on updating the IntentHistory: " + e.getMessage());
        }

        return State.Output.say(question);
        */
        return State.Output.sayNothing();
    }

    @Override
    public Output react(ArrayList<Interpretation> input) {

        /*

        Map<Integer, Interlocutor> persons = getContext().ACTIVE_INTERLOCUTORS.getValue();
        speakerCount = input.get(0).getSpeakerInfo().getSpeakerCount();


        String retAnswer = "";

        for(int i=0; i<speakerCount; i++){
            LOGGER.info("-> Retrieved Interlocutors: " + persons.get(i).getName());

            RandomList<String> answers;
            String answer = "I have no words";
            String result = InferResult(input.get(i));

            if (result != null && !result.equals("")) {
                LOGGER.info(" -> Inference was successful");
                answers = qaValues.getSuccessAnswers(selectedPredicate);
                persons.get(i).addInformation(selectedPredicate, result);
                getContext().ACTIVE_INTERLOCUTORS_UPDATER.updateValue(persons);
                LOGGER.info(" -> Updated Interlocutor: " + persons.get(i).getName());
            } else {
                LOGGER.warn(" -> Inference failed");
                answers = qaValues.getFailureAnswers(selectedPredicate);
                result = "";
                LOGGER.warn(" -> The result is empty. Nothing to store");
            }
            if (answers != null && !answers.isEmpty()) {
                answer = String.format(answers.getRandomElement(), result);
            } else {
                LOGGER.error(" -> The list of " + selectedPredicate + " answers is empty or null");
            }
            LOGGER.info(" -> Produced answer: " + answer);

            retAnswer = retAnswer + answer + persons.get(i).getName();
        }

        LOGGER.info(" -> Produced answer: " + retAnswer);
        nextState = getTransition(TRANSITION_INFO_OBTAINED);
        Segue s = new Segue(Segue.SegueType.CONNECTING_PHRASE, 0.5);
        return Output.say(retAnswer).setSegue(s);

        */
        return Output.sayNothing();
    }

    @Override
    public State getNextState() {
        return nextState;
    }

    private String InferResult(Interpretation input) {
        return getInference().inferRelationship(selectedPredicate, input);
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
}
