package roboy.newDialog.states;

import roboy.context.Context;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.Neo4jRelationships;
import roboy.memory.nodes.Interlocutor;
import roboy.memory.nodes.Interlocutor.RelationshipAvailability;
import static roboy.memory.nodes.Interlocutor.RelationshipAvailability.*;

import java.util.Set;

import static roboy.memory.Neo4jRelationships.*;

/**
 * This state will answer general questions.
 * The parser:
 * - provides triples generated from the question
 * - adds the answer to the question if there is an answer in DBpedia
 * This state:
 * - returns the answer if provided by the parser
 * - tries to query memory if there is no answer to the question
 * - queries the fallback if memory fails to answer as well
 *
 *
 * QuestionAnsweringState interface:
 * 1) Fallback is required.
 * 2) Outgoing transitions that have to be defined:
 *    - finishedQuestionAnswering:    following state if this state if finished with answering questions
 * 3) No parameters are used.
 */
public class QuestionAnsweringState extends State {

    private final static String TRANSITION_FINISHED_ANSWERING = "finishedQuestionAnswering";
    private final static String TRANSITION_ASK_INFO = "askPersonalQuestions";
    private final static String TRANSITION_LOOP_TO_KNOWN_PERSON = "askFollowUp";


    private State nextState;

    public QuestionAnsweringState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

    @Override
    public Output act() {
        return Output.say("QuestionAnsweringState act()");
    }

    @Override
    public Output react(Interpretation input) {
        Interlocutor person = Context.getInstance().ACTIVE_INTERLOCUTOR.getValue();

        // Let's sometimes go back to question asking
        double threshold = 0.8;
        if (Math.random() > threshold) {
            // loop back to previous states

            Neo4jRelationships[] predicates = { FROM, HAS_HOBBY, WORK_FOR, STUDY_AT };
            RelationshipAvailability availability = person.checkRelationshipAvailability(predicates);

            if (availability == SOME_AVAILABLE) {
                nextState = (Math.random() < 0.3) ? getTransition(TRANSITION_LOOP_TO_KNOWN_PERSON) : getTransition(TRANSITION_ASK_INFO);
            } else if (availability == NONE_AVAILABLE) {
                nextState = getTransition(TRANSITION_ASK_INFO);
            } else {
                nextState = getTransition(TRANSITION_LOOP_TO_KNOWN_PERSON);
            }

        } else {
            // stay in this state
            nextState = this;
        }

        return Output.say("QuestionAnsweringState react()");
    }

    @Override
    public State getNextState() {
        return nextState;
    }

    @Override
    protected Set<String> getRequiredTransitionNames() {
        return newSet(TRANSITION_FINISHED_ANSWERING);
    }

    @Override
    public boolean isFallbackRequired() {
        return true;
    }
}