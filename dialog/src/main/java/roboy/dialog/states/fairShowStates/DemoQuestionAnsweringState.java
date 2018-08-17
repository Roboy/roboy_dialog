package roboy.dialog.states.fairShowStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.dialog.Segue;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.Linguistics;
import roboy.linguistics.Triple;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.Neo4jRelationship;
import roboy.memory.nodes.MemoryNodeModel;
import roboy.memory.nodes.Roboy;
import roboy.talk.PhraseCollection;
import roboy.talk.Verbalizer;
import roboy.util.RandomList;

import java.util.List;


/**
 * Simple question answering state
 */
public class DemoQuestionAnsweringState extends State {

    private final static String TRANSITION_LONELY_ROBOY = "lonelyRoboy";
    private final static String TRANSITION_CHECK_OBJECTS = "talkAboutObjects";
    private final static String TRANSITION_FINISHED = "finished";
    private final static int MAX_NUM_OF_QUESTIONS = 3;

    private final Logger LOGGER = LogManager.getLogger();

    private State nextState = this;
    private int questionsAnswered = 0;
    private boolean askingSpecifyingQuestion = false;
    private String answerAfterUnspecifiedQuestion = "";

    public DemoQuestionAnsweringState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

    @Override
    public Output act() {

        if (questionsAnswered > 0) {
            return Output.say(PhraseCollection.QUESTION_ANSWERING_REENTERING.getRandomElement());
        }

        return Output.say("I'm pretty good at answering questions about myself and other stuff. What would you like to know?");

    }

    @Override
    public Output react(Interpretation input){

        if (askingSpecifyingQuestion) {

            askingSpecifyingQuestion = false;
            return reactToSpecifyingAnswer(input);

        } else{

            return reactToQuestion(input);
        }

    }

    @Override
    public State getNextState() {

        if(questionsAnswered > MAX_NUM_OF_QUESTIONS){

            nextState = getTransition(TRANSITION_FINISHED);
        } else {

            if (checkPersonListening()) {

                nextState = this;
                if (askingSpecifyingQuestion) { // we are asking a yes/no question --> stay in this state
                    nextState = this;
                } else if (Math.random() < 0.3) {
                    nextState = getTransition(TRANSITION_CHECK_OBJECTS);
                }

            } else {
                questionsAnswered = 0;
                nextState = getTransition((TRANSITION_LONELY_ROBOY));
            }
        }

        return nextState;
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
        if (input.getSentence() != null && input.getSentence().contains("yes")) {
            if (answerAfterUnspecifiedQuestion == null) {
                // parser could parse the question but did't provide an answer
                return Output.say("Not sure about the answer, " +
                        "but at least my amazing parser could understand you! ")
                        .setSegue(new Segue(Segue.SegueType.FLATTERY, 0.4));
            } else {
                // tell the response previously cached in answerAfterUnspecifiedQuestion
                return Output.say("In this case, " + PhraseCollection.QUESTION_ANSWERING_START.getRandomElement() + answerAfterUnspecifiedQuestion);
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

        Linguistics.ParsingOutcome parseOutcome = input.getParsingOutcome();
        if (parseOutcome == null) {
            LOGGER.error("Invalid parser outcome!");
            return Output.say("Invalid parser outcome!");
        }

        if (parseOutcome == Linguistics.ParsingOutcome.UNDERSPECIFIED) {

            // ambiguous question, luckily the parser has prepared a followup question
            // and maybe even an answer if we are lucky (we will check in reactToSpecifyingAnswer later)

            String question = input.getUnderspecifiedQuestion();
            answerAfterUnspecifiedQuestion = input.getAnswer(); // could be null, but that's fine for now

            askingSpecifyingQuestion = true; // next input should be a yes/no answer
            return Output.say("Could you be more precise, please? " + question);
        }

        if (parseOutcome == Linguistics.ParsingOutcome.SUCCESS) {
            if (input.getAnswer() != null) {
                // tell the answer, that was provided by the parser
                return Output.say(PhraseCollection.QUESTION_ANSWERING_START.getRandomElement() + " " + input.getAnswer());

            } else {
                // check for triple


                // parser could parse the question but has no answer
                return useMemoryOrFallback(input);
            }
        }

        // from here we know that dummyParserResult.equals("FAILURE")
        return useMemoryOrFallback(input);
    }

    private Output useMemoryOrFallback(Interpretation input) {
        try {
            if (input.getSemTriples() != null) {
                Output memoryAnswer = answerFromMemory(input.getSemTriples());
                if (memoryAnswer != null) return memoryAnswer;
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage());
        }

        return Output.useFallback();
    }


    private Output answerFromMemory(List<Triple> triples) {

        // try to use memory to answer
        Roboy roboy = new Roboy(getMemory());

        if (triples.size() == 0) {
            return null;
        }

        String answer = "I like " + inferMemoryAnswer(triples, roboy) + "humans. ";
        return Output.say(answer);
    }

    private String inferMemoryAnswer(List<Triple> triples, Roboy roboy) {
        String answer = "";
        for (Triple result : triples) {

            if (result.predicate != null) {
                if (result.predicate.contains(Neo4jRelationship.HAS_HOBBY.type)) {
                    RandomList<MemoryNodeModel> nodes = getMemNodesByIds(roboy.getRelationships(Neo4jRelationship.HAS_HOBBY));
                    if (!nodes.isEmpty()) {
                        for (MemoryNodeModel node : nodes) {
                            answer += node.getProperties().get("name").toString() + " and ";
                        }
                    }
                    break;

                } else if (result.predicate.contains(Neo4jRelationship.FRIEND_OF.type)) {
                    answer += "my friends ";
                    RandomList<MemoryNodeModel> nodes = getMemNodesByIds(roboy.getRelationships(Neo4jRelationship.FRIEND_OF));
                    if (!nodes.isEmpty()) {
                        for (MemoryNodeModel node : nodes) {
                            answer += node.getProperties().get("name").toString() + " and ";
                        }
                    }
                    answer += " other ";
                    break;
                }
            }
        }
        return answer;
    }

    @Override
    public boolean isFallbackRequired() {
        return true;
    }

    private boolean checkPersonListening(){

        try {

            return getContext().PERSON_DETECTION.getLastValue().getData();

        } catch(NullPointerException e){
            LOGGER.info("Make sure person listening is publishing, receiving: " + e.getMessage());
            return false;
        }
    }

}
