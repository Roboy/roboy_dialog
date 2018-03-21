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
import roboy.util.RandomList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static roboy.memory.Neo4jRelationships.*;

/**
 * Follow Up Asking State
 * This state is only entered if there are some known facts about the active interlocutor.
 * It asks if the known facts are still up to date.
 */
public class FUAState extends State {

    private QAJsonParser qaValues;
    private Neo4jRelationships[] predicates = { FROM, HAS_HOBBY, WORK_FOR, STUDY_AT };
    private Neo4jRelationships selectedPredicate;
    private State nextState;

    private final String TRANSITION_INFO_UPDATED = "questionAnswering";
    private final Logger LOGGER = LogManager.getLogger();

    public FUAState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
        qaValues = new QAJsonParser(params.getParameter("qaFile"));
    }

    @Override
    public State.Output act() {
        Interlocutor person = Context.getInstance().ACTIVE_INTERLOCUTOR.getValue();

        for (Neo4jRelationships predicate : predicates) {
            if (person.hasRelationship(predicate) && !Context.getInstance().DIALOG_INTENTS.contains(predicate.type)) {
                selectedPredicate = predicate;
                break;
            }
        }

        if (selectedPredicate != null) {
            RandomList<String> questions = new RandomList<>(qaValues.getFollowUpQuestions(selectedPredicate));
            String retrievedResult = "";
            RandomList<Integer> ids = new RandomList<>(person.getRelationships(selectedPredicate));
            if (!ids.isEmpty()) {
                Neo4jMemoryInterface memory = getParameters().getMemory();
                try {
                    Gson gson = new Gson();
                    String requestedObject = memory.getById(ids.getRandomElement());
                    MemoryNodeModel node = gson.fromJson(requestedObject, MemoryNodeModel.class);
                    retrievedResult = node.getProperties().get("name").toString();
                } catch (InterruptedException | IOException e) {
                    LOGGER.error("Error on Memory data retrieval: " + e.getMessage());
                }
            }
            String question = String.format(questions.getRandomElement(), retrievedResult);
            Context.getInstance().DIALOG_INTENTS_UPDATER.updateValue(selectedPredicate.type);
            return Output.say(question);
        } else {
            return Output.sayNothing();
        }
    }

    @Override
    public State.Output react(Interpretation input) {
        // TODO: How do we ensure it is the same Interlocutor in the same state of existence?
        Interlocutor person = Context.getInstance().ACTIVE_INTERLOCUTOR.getValue();
        String answer = "I have no words";

        // TODO: Will need to consider proper conditions for processing
        if (selectedPredicate != null) {
            // TODO: Perform updating the person object
            Context.getInstance().ACTIVE_INTERLOCUTOR_UPDATER.updateValue(person);
            List<String> answers = qaValues.getFollowUpAnswers(selectedPredicate);
            if (answers != null && !answers.isEmpty()) {
                answer = String.format(answers.get((int)(Math.random() * answers.size())), "");
            }
        }

        nextState = getTransition(TRANSITION_INFO_UPDATED);

        return Output.say(answer);
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
        return newSet("qaFile");
    }
}
