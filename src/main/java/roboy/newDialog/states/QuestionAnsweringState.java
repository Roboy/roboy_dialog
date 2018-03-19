package roboy.newDialog.states;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import java.util.List;
import java.util.Map;
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

    private final Logger logger = LogManager.getLogger();

    private final static String TRANSITION_FINISHED_ANSWERING = "finishedQuestionAnswering";
    private final static String TRANSITION_LOOP_TO_NEW_PERSON = "loopToNewPerson";
    private final static String TRANSITION_LOOP_TO_KNOWN_PERSON = "loopToKnownPerson";

    private final static int MAX_NUM_OF_QUESTIONS = 5;
    private int questionsAnswered = 0;

    private final static RandomList<String> reenteringPhrases = new RandomList<>(
            "Is there anything else you would like to know?",
            "Is that all that comes to your mind? Ask me something!",
            "Oh well, I know much more stuff, just ask me a question.",
		    "Let me uncover my potential. Ask something really difficult!");

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

        if (questionsAnswered > 0) {
            return Output.say("[reentering, q answered: " + questionsAnswered + "] " + reenteringPhrases.getRandomElement());
        }
        return Output.say("[first entry] I'm pretty good at answering questions about myself and other stuff. What would you like to know?");
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

        askingSpecifyingQuestion = false;

        // check if answer is yes
        if (((String) input.getFeature(Linguistics.SENTENCE)).contains("yes")) {
            // tell the response previously cached in answerToTheBestUnspecifiedCandidate
            return Output.say("In this case, I think the answer is " + answerToTheBestUnspecifiedCandidate);

        } else {
            // the answer is no. we don't ask more specifying questions
            // use avoid answer segue
            return Output.say("replace with say nothing [answer to specifying got NO]")
                    .setSegue(new Segue(Segue.SegueType.AVOID_ANSWER, 1));
        }
    }



    private Output reactToQuestion(Interpretation input) {


        askingSpecifyingQuestion = false;
        questionsAnswered++;

        Linguistics.PARSER_OUTCOME parseOutcome = (Linguistics.PARSER_OUTCOME) input.getFeature(Linguistics.PARSER_RESULT);
        if (parseOutcome == null) {
            logger.error("Invalid parser outcome!");
            return Output.say("Invalid parser outcome!");
        }

        if (parseOutcome == Linguistics.PARSER_OUTCOME.UNDERSPECIFIED) {

            // ambiguous question, luckily the parser has prepared a followup question

            Object underspQuestionObj = input.getFeature(Linguistics.UNDERSPECIFIED_QUESTION);

            if (!(underspQuestionObj instanceof String)) {
                logger.error("UNDERSPECIFIED_QUESTION is not a String");
                return Output.say("Well, looks like my parser has some problems: something is wrong with the types.");
            }

            // answer will be in input.getFeature(Linguistics.UNDERSPECIFIED_ANSWER);
            // TODO: get answer


            String question = (String) underspQuestionObj;
            answerToTheBestUnspecifiedCandidate = "TODO"; // save answer for later

            askingSpecifyingQuestion = true; // next input should be a yes/no answer
            return Output.say("Could you be more precise, please? " + question);
        }

        if (parseOutcome == Linguistics.PARSER_OUTCOME.SUCCESS) {
            // tell the answer, that was provided by the parser
            return Output.say("I think, the answer is " + input.getFeature(Linguistics.PARSE_ANSWER));
        }

        // from here we know that dummyParserResult.equals("FAILURE")
        // another check to be safe
        if (parseOutcome != Linguistics.PARSER_OUTCOME.FAILURE) {
            logger.error("This is very wrong! parseOutcome != Linguistics.PARSER_OUTCOME.FAILURE");
            return Output.say("This is very wrong! parseOutcome != Linguistics.PARSER_OUTCOME.FAILURE");
        }

        // try to use memory to answer
        Neo4jMemoryInterface mem = getParameters().getMemory();
        // mem.answer(input) // TODO

        return Output.say("[parser failure] reply from memory or fallback");

        // if memory has no answer, use fallback
        //return Output.useFallback();
    }

    @Override
    public State getNextState() {

        if (askingSpecifyingQuestion) { // we are asking a yes/no question --> stay in this state
            logger.info("askingSpecifyingQuestion, staying in same state");
            return this;

        } else if (questionsAnswered > MAX_NUM_OF_QUESTIONS) { // enough questions answered --> finish asking
            logger.info("enough questions answered, DONE");

            return getTransition(TRANSITION_FINISHED_ANSWERING);

        } else if (Math.random() < 1) { // loop back to previous states with probability 0.2
            logger.info("random loopback to previous");

            Interlocutor person = Context.getInstance().ACTIVE_INTERLOCUTOR.getValue();
            Neo4jRelationships[] predicates = { FROM, HAS_HOBBY, WORK_FOR, STUDY_AT };
            RelationshipAvailability availability = person.checkRelationshipAvailability(predicates);

            if (availability == SOME_AVAILABLE) {
                return (Math.random() < 0.3) ? getTransition(TRANSITION_LOOP_TO_KNOWN_PERSON) : getTransition(TRANSITION_LOOP_TO_NEW_PERSON);
            } else if (availability == NONE_AVAILABLE) {
                return getTransition(TRANSITION_LOOP_TO_NEW_PERSON);
            } else {
                if (!isIntentsHistoryComplete(predicates)) {
                    return getTransition(TRANSITION_LOOP_TO_KNOWN_PERSON);
                } else {
                    return this;
                }
            }

        } else { // stay in this state
            logger.info("decided to stay and answer another (reentering)");
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

    private boolean isIntentsHistoryComplete(Neo4jRelationships[] predicates) {
        boolean isComplete = true;
        for (Neo4jRelationships predicate : predicates) {
            if (!Context.getInstance().DIALOG_INTENTS.contains(predicate.type)) {
                isComplete = false;
            }
        }
        return isComplete;
    }
}