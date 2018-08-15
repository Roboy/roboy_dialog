package roboy.dialog.states.ordinaryStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.context.contextObjects.IntentValue;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.Linguistics;
import roboy.linguistics.Triple;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.Neo4jRelationship;
import roboy.memory.nodes.Interlocutor;
import roboy.memory.nodes.Interlocutor.RelationshipAvailability;
import roboy.memory.nodes.MemoryNodeModel;
import roboy.memory.nodes.Roboy;
import roboy.dialog.Segue;
import roboy.talk.PhraseCollection;
import roboy.talk.Verbalizer;
import roboy.util.RandomList;

import static roboy.memory.nodes.Interlocutor.RelationshipAvailability.*;

import java.util.List;
import java.util.Set;

import static roboy.memory.Neo4jRelationship.*;

/**
 * This state will answer generalStates questions.
 * The parser:
 * - provides triples generated from the question
 * - adds the answer to the question if there is an answer in DBpedia
 * - tells a specifying followup question if the interlocutor's question was ambiguous
 *
 * This state:
 * - checks if interlocutor wants to play a game
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
    private final static String TRANSITION_TO_GAME = "switchToGaming";
    private final static int MAX_NUM_OF_QUESTIONS = 5;
    private int questionsAnswered = 0;

    private final static RandomList<String> reenteringPhrases = PhraseCollection.QUESTION_ANSWERING_REENTERING;
    private final static RandomList<String> answerStartingPhrases = PhraseCollection.QUESTION_ANSWERING_START;

    private boolean askingSpecifyingQuestion = false;
    private String answerAfterUnspecifiedQuestion = ""; // the answer to use if specifying question is answered with YES
    private boolean userWantsGame = false;

    private final static double THRESHOLD_BORED = 0.3;
    private boolean roboySuggestedGame = false;

    public QuestionAnsweringState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

    @Override
    public Output act() {

        if(Math.random() > THRESHOLD_BORED && questionsAnswered > 2){
            roboySuggestedGame = true;
            return Output.say(PhraseCollection.OFFER_GAME_PHRASES.getRandomElement());
        }
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

        if(roboySuggestedGame && getInference().inferSentiment(input) == Linguistics.UtteranceSentiment.POSITIVE){
            roboySuggestedGame = false;
            userWantsGame = true;
            return Output.say(Verbalizer.startSomething.getRandomElement());
        }

        if(userWantsGameCheck(input)) {
            return Output.say(Verbalizer.startSomething.getRandomElement());

        }else if (askingSpecifyingQuestion) {
                askingSpecifyingQuestion = false;
                return reactToSpecifyingAnswer(input);

        } else{
            return reactToQuestion(input);
        }

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

        Linguistics.ParsingOutcome parseOutcome = input.getParsingOutcome();
        if (parseOutcome == null) {
            logger.error("Invalid parser outcome!");
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
                return Output.say(answerStartingPhrases.getRandomElement() + " " + input.getAnswer());

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

        if(userWantsGame){
            userWantsGame = false;
            return getTransition(TRANSITION_TO_GAME);

        } else if (askingSpecifyingQuestion) { // we are asking a yes/no question --> stay in this state
            return this;

        } else if (questionsAnswered > MAX_NUM_OF_QUESTIONS) { // enough questions answered --> finish asking
            return getTransition(TRANSITION_FINISHED_ANSWERING);

        } else if (Math.random() < 0.5) { // loop back to previous states with probability 0.5

            Interlocutor person = getContext().ACTIVE_INTERLOCUTOR.getValue();
            Neo4jRelationship[] predicates = { FROM, HAS_HOBBY, WORK_FOR, STUDY_AT };
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
            return this;
        }

    }

    private Output useMemoryOrFallback(Interpretation input) {
        try {
            if (input.getSemTriples() != null) {
                Output memoryAnswer = answerFromMemory(input.getSemTriples());
                if (memoryAnswer != null) return memoryAnswer;
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
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

    private boolean userWantsGameCheck(Interpretation input){

        userWantsGame = false;

        List<String> tokens = input.getTokens();
        if (tokens != null && !tokens.isEmpty()){
            if(tokens.contains("game") || tokens.contains("play") || tokens.contains("games")){
                userWantsGame = true;
            }
        }
        return userWantsGame;
    }

    @Override
    protected Set<String> getRequiredTransitionNames() {
        return newSet(TRANSITION_FINISHED_ANSWERING, TRANSITION_LOOP_TO_NEW_PERSON, TRANSITION_LOOP_TO_KNOWN_PERSON);
    }

    @Override
    public boolean isFallbackRequired() {
        return true;
    }

    private boolean isIntentsHistoryComplete(Neo4jRelationship[] predicates) {
        boolean isComplete = true;
        for (Neo4jRelationship predicate : predicates) {
            if (!getContext().DIALOG_INTENTS.contains(new IntentValue(PersonalInformationFollowUpState.INTENTS_HISTORY_ID, predicate))) {
                isComplete = false;
            }
        }
        return isComplete;
    }
}