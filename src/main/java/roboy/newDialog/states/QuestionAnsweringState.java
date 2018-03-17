package roboy.newDialog.states;

import roboy.context.Context;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.Neo4jMemoryInterface;
import roboy.memory.Neo4jRelationships;
import roboy.memory.nodes.Interlocutor;
import roboy.memory.nodes.Interlocutor.RelationshipAvailability;
import roboy.memory.nodes.MemoryNodeModel;
import roboy.newDialog.Segue;
import roboy.util.RandomList;

import static roboy.memory.nodes.Interlocutor.RelationshipAvailability.*;

import java.util.Set;

import static roboy.memory.Neo4jRelationships.*;

/**
 * This state will answer general questions.
 * The parser:
 * - provides triples generated from the question
 * - adds the answer to the question if there is an answer in DBpedia
 * - tells a specifying followup question if the interlocutor's question was ambiguous
 *
 * This state:
 * - returns the answer if provided by the parser
 * - asks the specifying followup question if provided by the parser
 * -    - if answered with yes --> will use the parser again to get the answer to the original question
 *      - if answered with no --> will use a segue to avoid answer
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
    private final static String TRANSITION_LOOP_TO_NEW_PERSON = "loopToNewPerson";
    private final static String TRANSITION_LOOP_TO_KNOWN_PERSON = "loopToKnownPerson";

    private final static int MAX_NUM_OF_QUESTIONS = 5;
    private int questionsAnswered = 0;

    private final static RandomList<String> reenteringPhrases = new RandomList<>(
            "anything else",
            "Is that all that comes to your mind?",
            "Oh well, I know much more stuff",
		    "Let me uncover my potential. Ask something really difficult");
    private boolean reenteringItself = false; // indicates that this state is reentered from itself

    private boolean askingSpecifyingQuestion = false;
    private String answerToTheBestUnspecifiedCandidate = ""; // the answer to use if specifying question is answered with YES


    public QuestionAnsweringState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

    @Override
    public Output act() {
        if (askingSpecifyingQuestion) {
            return Output.sayNothing();
        }

        if (reenteringItself) {
            return Output.say(reenteringPhrases.getRandomElement());
        }
        return Output.say("I'm pretty good at answering questions about myself and other stuff. What would you like to know?");
    }

    @Override
    public Output react(Interpretation input) {

        if (askingSpecifyingQuestion) {
            askingSpecifyingQuestion = false;
            return reactToSpecifyingAnswer(input);
        }

        return reactToQuestion(input);


    }

    /**
     * React to answer of the specifying question asked previously.
     *
     * @param input something like "yes" or "no"
     * @return answer to the answer to the original question if specifying question was answered with 'yes'
     */
    private Output reactToSpecifyingAnswer(Interpretation input) {

        // check if answer is yes
        if (((String) input.getFeature(Linguistics.SENTENCE)).contains("yes")) {
            // send formula to the parser
            //   something like  parser.parse(specifyingQuestionFormula);
            // get the response
            // get the answer out the response
            // tell the response
            return Output.say(answerToTheBestUnspecifiedCandidate);

        } else {
            // the answer is no. we don't ask more specifying questions
            // use avoid answer segue
            return Output.sayNothing().setSegue(new Segue(Segue.SegueType.AVOID_ANSWER, 1));
        }
    }

    private Output reactToQuestion(Interpretation input) {

        // Check if the semantic parser has an answer
        // it should contain an enum ParsingResult \in { PARSE_SUCCESS, PARSE_FAILURE, UNDERSPECIFIED_ANSWER }
        String dummyParserResult = Math.random() < .33 ? "SUCCESS" : (Math.random() < 0.5 ? "FAILURE" : "UNDERSPECIFIED");

        if (dummyParserResult.equals("UNDERSPECIFIED")) {
            // ambiguous question, luckily the parser has prepared a followup question
            String specifyingQuestion = (String) input.getFeature("specifyingQuestion");
            answerToTheBestUnspecifiedCandidate = (String) input.getFeature("answerToTheBestUnspecifiedCandidate"); // save for later
            askingSpecifyingQuestion = true;
            return Output.say("could you be more precise, please? " + specifyingQuestion);
        }

        askingSpecifyingQuestion = false;
        questionsAnswered++;

        if (dummyParserResult.equals("SUCCESS")) {
            // tell the answer, that was provided by the parser
            return Output.say("parser says: " + input.getFeature("get result from parser here"));
        }

        // from here we know that dummyParserResult.equals("FAILURE")

        // try to use memory to answer
        Neo4jMemoryInterface mem = getParameters().getMemory();
        // mem.answer(input) // TODO


        // if memory has no answer, use fallback
        return Output.useFallback();
    }

    @Override
    public State getNextState() {

        reenteringItself = false;

        if (questionsAnswered >= MAX_NUM_OF_QUESTIONS) { // enough questions answered --> finish asking

            return getTransition(TRANSITION_FINISHED_ANSWERING);

        } else if (Math.random() < 0.2) { // loop back to previous states with probability 0.2

            Interlocutor person = Context.getInstance().ACTIVE_INTERLOCUTOR.getValue();
            Neo4jRelationships[] predicates = { FROM, HAS_HOBBY, WORK_FOR, STUDY_AT };
            RelationshipAvailability availability = person.checkRelationshipAvailability(predicates);

            if (availability == SOME_AVAILABLE) {
                return (Math.random() < 0.3) ? getTransition(TRANSITION_LOOP_TO_KNOWN_PERSON) : getTransition(TRANSITION_LOOP_TO_NEW_PERSON);
            } else if (availability == NONE_AVAILABLE) {
                return getTransition(TRANSITION_LOOP_TO_NEW_PERSON);
            } else {
                return getTransition(TRANSITION_LOOP_TO_KNOWN_PERSON);
            }

        } else { // stay in this state

            reenteringItself = true;
            return this;

        }

    }

    @Override
    protected Set<String> getRequiredTransitionNames() {
        return newSet(TRANSITION_FINISHED_ANSWERING, TRANSITION_LOOP_TO_NEW_PERSON, TRANSITION_LOOP_TO_KNOWN_PERSON);
    }

    @Override
    public boolean isFallbackRequired() {
        return true;
    }
}