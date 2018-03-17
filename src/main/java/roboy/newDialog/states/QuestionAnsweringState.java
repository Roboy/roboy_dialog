package roboy.newDialog.states;

import roboy.context.Context;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.Neo4jRelationships;
import roboy.memory.nodes.Interlocutor;

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
    private final static String TRANSITION_FOLLOW_UP = "askFollowUp";


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
        double treshold = 0.8;
        if (Math.random() > treshold) {
            Neo4jRelationships[] predicates = { FROM, HAS_HOBBY, WORK_FOR, STUDY_AT };
            Boolean infoPurity = person.checkInfoPurity3VL(predicates);
            if (infoPurity == null) {
                nextState = (Math.random() < 0.3) ? getTransition(TRANSITION_FOLLOW_UP) : getTransition(TRANSITION_ASK_INFO);
            } else {
                nextState = infoPurity ? getTransition(TRANSITION_FOLLOW_UP) : getTransition(TRANSITION_ASK_INFO);
            }
        } else {
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