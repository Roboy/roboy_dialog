package roboy.newDialog.states;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.context.Context;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.Neo4jMemoryInterface;
import roboy.memory.Neo4jRelationships;
import roboy.memory.nodes.Interlocutor;
import roboy.memory.nodes.MemoryNodeModel;
import roboy.util.QAJsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static roboy.memory.Neo4jRelationships.*;

/**
 * Follow Up Asking State
 */
public class FUAState extends State{
    private QAJsonParser qaValues;
    private Neo4jRelationships[] predicates = { FROM, HAS_HOBBY, WORK_FOR, STUDY_AT };
    private Neo4jRelationships selectedPredicate;
    private State nextState;

    private final String TRANSITION_QUESTION_ANSWERING = "questionAnswering";
    private final Logger LOGGER = LogManager.getLogger();

    public FUAState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
        qaValues = new QAJsonParser(params.getParameter("qaFile"));
    }

    @Override
    public State.Output act() {
        Interlocutor person = Context.getInstance().ACTIVE_INTERLOCUTOR.getValue();

        for (Neo4jRelationships predicate : predicates) {
            if (person.hasRelationship(predicate)) {
                selectedPredicate = predicate;
                break;
            }
        }

        List<String> questions = qaValues.getFollowUpQuestions(selectedPredicate);
        String retrievedResult = "";
        ArrayList<Integer> ids = person.getRelationships(selectedPredicate);
        if (ids != null && !ids.isEmpty()) {
            Neo4jMemoryInterface memory = getParameters().getMemory();
            try {
                Gson gson = new Gson();
                String requestedObject = memory.getById(ids.get(0));
                MemoryNodeModel node = gson.fromJson(requestedObject, MemoryNodeModel.class);
                retrievedResult = node.getProperties().get("name").toString();
            } catch (InterruptedException | IOException e) {
                LOGGER.error("Error on Memory data retrieval: " + e.getMessage());
            }
        }
        String question = String.format(questions.get((int) (Math.random() * questions.size())), retrievedResult);
        Context.getInstance().DIALOG_INTENTS_UPDATER.updateValue(selectedPredicate.type);
        return Output.say(question);
    }

    @Override
    public State.Output react(Interpretation input) {
        // TODO: How do we ensure it is the same Interlocutor in the same state of existence?
        Interlocutor person = Context.getInstance().ACTIVE_INTERLOCUTOR.getValue();
        String answer = "I have no words";

        // TODO: What is the condition on interpretation?
        if (true) {
            // TODO: Perform updating the person object
            Context.getInstance().ACTIVE_INTERLOCUTOR_UPDATER.updateValue(person);
            List<String> answers = qaValues.getFollowUpAnswers(selectedPredicate);
            if (answers != null && !answers.isEmpty()) {
                answer = String.format(answers.get((int)(Math.random() * answers.size())), "");
            }
        }

        nextState = getTransition(TRANSITION_QUESTION_ANSWERING);

        return Output.say(answer);
    }

    @Override
    public State getNextState() {
        return nextState;
    }

    @Override
    protected Set<String> getRequiredTransitionNames() {
        // optional: define all required transitions here:
        return newSet(TRANSITION_QUESTION_ANSWERING);
    }

    @Override
    protected Set<String> getRequiredParameterNames() {
        return newSet("qaFile");
    }
}
