package roboy.newDialog.states;

import roboy.linguistics.sentenceanalysis.Interpretation;

import java.util.Set;

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

    private final static String TRANSITION_FINISHED_QA = "finishedQuestionAnswering";

    public QuestionAnsweringState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

    @Override
    public Output act() {
        return Output.say("QuestionAnsweringState act()");
    }

    @Override
    public Output react(Interpretation input) {
        return Output.say("QuestionAnsweringState react()");
    }

    @Override
    public State getNextState() {
        return getTransition(TRANSITION_FINISHED_QA);
    }

    @Override
    protected Set<String> getRequiredTransitionNames() {
        return newSet(TRANSITION_FINISHED_QA);
    }

    @Override
    public boolean isFallbackRequired() {
        return true;
    }
}
