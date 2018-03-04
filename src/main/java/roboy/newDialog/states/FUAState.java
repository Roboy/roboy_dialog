package roboy.newDialog.states;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.context.Context;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.Neo4jMemory;
import roboy.memory.Neo4jRelationships;
import roboy.memory.nodes.Interlocutor;
import roboy.memory.nodes.MemoryNodeModel;
import roboy.util.PFUAValues;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static roboy.memory.Neo4jRelationships.*;

/**
 * Follow Up Asking State
 */
public class FUAState extends State{
    private PFUAValues qaValues;
    private Neo4jRelationships[] predicates = { FROM, HAS_HOBBY, WORK_FOR, STUDY_AT };
    private int selectedPredicateIndex = 0;

    final Logger LOGGER = LogManager.getLogger();

    public FUAState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
        qaValues = new PFUAValues(params.getParameter("qaFile"));
    }

    @Override
    public State.Output act() {
        Interlocutor person = Context.Values.ACTIVE_INTERLOCUTOR.getValue();

        selectedPredicateIndex = (int)(Math.random() * predicates.length);
        List<String> questions = qaValues.getEntry(predicates[selectedPredicateIndex]).getFUP().get("Q");
        String retrievedResult = "";
        ArrayList<Integer> ids = person.getRelationships(predicates[selectedPredicateIndex]);
        if (ids != null && !ids.isEmpty()) {
            Neo4jMemory memory = Neo4jMemory.getInstance();
            try {
                MemoryNodeModel requestedObject = memory.getById(ids.get(0));
                retrievedResult = requestedObject.getProperties().get("name").toString();
            } catch (InterruptedException | IOException e) {
                LOGGER.error("Error on Memory data retrieval: " + e.getMessage());
            }
        } else {
            LOGGER.info("Go to PersonalQA state");
        }
        String question = String.format(questions.get((int)(Math.random()*questions.size())), retrievedResult);
        return State.Output.say(question);
    }

    @Override
    public State.Output react(Interpretation input) {
        List<String> answers;
        String answer = "I have no words";
        // TODO: What is the condition?
        if (true) {
            answers = qaValues.getEntry(predicates[selectedPredicateIndex]).getFUP().get("A");
        }
        if (answers != null && !answers.isEmpty()) {
            answer = String.format(answers.get((int) (Math.random() * answers.size())), "");
        }
        return State.Output.say(answer);
    }

    @Override
    public State getNextState() {
        return getTransition("next");
    }

    @Override
    protected Set<String> getRequiredTransitionNames() {
        // optional: define all required transitions here:
        return newSet("next");
    }

    @Override
    protected Set<String> getRequiredParameterNames() {
        return newSet("qaFile");
    }
}
