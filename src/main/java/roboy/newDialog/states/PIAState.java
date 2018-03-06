package roboy.newDialog.states;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.context.Context;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.Neo4jRelationships;
import roboy.memory.nodes.Interlocutor;
import roboy.util.QAJsonParser;

import java.util.List;
import java.util.Set;

import static roboy.memory.Neo4jRelationships.*;

/**
 * Personal Information Asking State
 */
public class PIAState extends State {
    private QAJsonParser qaValues;
    private Neo4jRelationships[] predicates = { FROM, HAS_HOBBY, WORK_FOR, STUDY_AT };
    private int selectedPredicateIndex = 0;
    private State nextState;

    private final String next = "next";
    final Logger LOGGER = LogManager.getLogger();

    public PIAState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
        qaValues = new QAJsonParser(params.getParameter("qaFile"));
    }

    @Override
    public State.Output act() {
        Interlocutor person = Context.getInstance().ACTIVE_INTERLOCUTOR.getValue();

        // TODO: Check for white spaces in the Interlocutor node and select the predicate
        // TODO: We need the predicate itself, not the index
        selectedPredicateIndex = (int)(Math.random() * predicates.length);
        List<String> questions = qaValues.getEntry(predicates[selectedPredicateIndex]).getQuestions();
        String question = questions.get((int)(Math.random()*questions.size()));
        Context.getInstance().DIALOG_INTENTS_UPDATER.updateValue(predicates[selectedPredicateIndex].type);
        return State.Output.say(question);
    }

    @Override
    public State.Output react(Interpretation input) {
        // TODO: Get relevant data from the input
        // TODO: Update the Interlocutor
        // TODO: Update the Memory
        List<String> answers;
        String answer = "I have no words";
        // TODO: What is the condition?
        if (true) {
            answers = qaValues.getEntry(predicates[selectedPredicateIndex]).getAnswers().get("SUCCESS");
        } else {
            answers = qaValues.getEntry(predicates[selectedPredicateIndex]).getAnswers().get("FAILURE");
        }
        if (answers != null && !answers.isEmpty()) {
            answer = String.format(answers.get((int) (Math.random() * answers.size())), "");
        }
        nextState = getTransition(next);
        return State.Output.say(answer);
    }

    @Override
    public State getNextState() {
        return nextState;
    }

    @Override
    protected Set<String> getRequiredTransitionNames() {
        // optional: define all required transitions here:
        return newSet(next);
    }

    @Override
    protected Set<String> getRequiredParameterNames() {
        return newSet("qaFile");
    }
}
