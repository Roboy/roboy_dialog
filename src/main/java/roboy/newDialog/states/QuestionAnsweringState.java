package roboy.newDialog.states;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.context.Context;
import roboy.context.contextObjects.IntentValue;
import roboy.linguistics.Linguistics;
import roboy.linguistics.Triple;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.Neo4jMemoryInterface;
import roboy.memory.Neo4jRelationships;
import roboy.memory.nodes.Interlocutor;
import roboy.memory.nodes.Interlocutor.RelationshipAvailability;
import roboy.memory.nodes.MemoryNodeModel;
import roboy.memory.nodes.Roboy;
import roboy.newDialog.Segue;
import roboy.util.RandomList;

import static roboy.memory.nodes.Interlocutor.RelationshipAvailability.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

    private final static RandomList<String> answerStartingPhrases = new RandomList<>(
            "I think, the answer is ",
            "I am sure that it is ",
            "How about ",
            "I know for sure, it must be "
    );

    private boolean askingSpecifyingQuestion = false;
    private String answerAfterUnspecifiedQuestion = ""; // the answer to use if specifying question is answered with YES


    public QuestionAnsweringState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

    @Override
    public Output act() {
        if (askingSpecifyingQuestion) {
            return Output.sayNothing();
        }

        if (questionsAnswered > 0) {
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

        askingSpecifyingQuestion = false;

        // check if answer is yes
        if (((String) input.getFeature(Linguistics.SENTENCE)).contains("yes")) {
            if (answerAfterUnspecifiedQuestion == null) {
                // parser could parse the question but did't provide an answer
                return Output.say("Not sure about the answer, " +
                        "but at least my amazing parser could understand you! ")
                        .setSegue(new Segue(Segue.SegueType.FLATTERY, 0.4));
            } else {
                // tell the response previously cached in answerAfterUnspecifiedQuestion
                return Output.say("In this case, " + answerStartingPhrases.getRandomElement() + answerAfterUnspecifiedQuestion);
            }

        } else {
            // the answer is no. we don't ask more specifying questions
            // use avoid answer segue
            return Output.sayNothing().setSegue(new Segue(Segue.SegueType.AVOID_ANSWER, 1));
        }
    }



    private Output reactToQuestion(Interpretation input) {

        askingSpecifyingQuestion = false;
        questionsAnswered++;

        Linguistics.PARSER_OUTCOME parseOutcome = input.parserOutcome;
        if (parseOutcome == null) {
            logger.error("Invalid parser outcome!");
            return Output.say("Invalid parser outcome!");
        }

        if (parseOutcome == Linguistics.PARSER_OUTCOME.UNDERSPECIFIED) {

            // ambiguous question, luckily the parser has prepared a followup question
            // and maybe even an answer if we are lucky (we will check in reactToSpecifyingAnswer later)

            String question = input.underspecifiedQuestion;
            answerAfterUnspecifiedQuestion = input.answer; // could be null, but that's fine for now

            askingSpecifyingQuestion = true; // next input should be a yes/no answer
            return Output.say("Could you be more precise, please? " + question);
        }

        if (parseOutcome == Linguistics.PARSER_OUTCOME.SUCCESS) {
            if (input.answer != null) {
                // tell the answer, that was provided by the parser
                return Output.say(answerStartingPhrases.getRandomElement() + " " + input.answer);

            } else {
                // check for triple


                // parser could parse the question but has no answer
                return useMemoryOrFallback(input);
            }
        }

        // from here we know that dummyParserResult.equals("FAILURE")
        return useMemoryOrFallback(input);
    }

    @Override
    public State getNextState() {

        if (askingSpecifyingQuestion) { // we are asking a yes/no question --> stay in this state
            //logger.info("askingSpecifyingQuestion, staying in same state");
            return this;

        } else if (questionsAnswered > MAX_NUM_OF_QUESTIONS) { // enough questions answered --> finish asking
            //logger.info("enough questions answered, DONE");

            return getTransition(TRANSITION_FINISHED_ANSWERING);

        } else if (Math.random() < 0.5) { // loop back to previous states with probability 0.5
            //logger.info("random loopback to previous");

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
            //logger.info("decided to stay and answer another (reentering)");
            return this;

        }

    }


    private Output useMemoryOrFallback(Interpretation input) {
        Output memoryAnswer = tryToAnswerWithMemory(input, input.semParserTriples);
        if (memoryAnswer != null) return memoryAnswer;

        return Output.useFallback();
    }

    private Output tryToAnswerWithMemory(Interpretation input, List<Triple> triples) {
        //if (input.getFeatures().get(Linguistics))

        // try to use memory to answer
        Roboy roboy = new Roboy(getParameters().getMemory());
        // Wagram, do some magic here
        List<Triple> results = (List<Triple>) input.getFeatures().get(Linguistics.SEM_TRIPLE);
        if (results.size() != 0) {
            for (Triple result : results) {
                if (result.subject.contains("roboy"))
                    if (result.predicate.contains(Neo4jRelationships.HAS_HOBBY.type)) {
                        if (result.object == null) {
                            String answer = "I like ";
                            ArrayList<Integer> ids = roboy.getRelationships(Neo4jRelationships.HAS_HOBBY);
                            if (ids != null && !ids.isEmpty()) {
                                try {
                                    Gson gson = new Gson();
                                    String requestedObject = getParameters().getMemory().getById(ids.get(0));
                                    MemoryNodeModel node = gson.fromJson(requestedObject, MemoryNodeModel.class);
                                    answer += node.getProperties().get("name").toString() + " and ";
                                } catch (InterruptedException | IOException e) {
                                    logger.error("Error on Memory data retrieval: " + e.getMessage());
                                }
                            }
                            answer += "humans. They are cute!";
                            return Output.say(answer);
                        }
                    }
                }
            }
        // we could also reuse some functionality from the old QuestionAnsweringState

        return null;
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
            if (!Context.getInstance().DIALOG_INTENTS.contains(new IntentValue("FUP", predicate))) {
                isComplete = false;
            }
        }
        return isComplete;
    }
}