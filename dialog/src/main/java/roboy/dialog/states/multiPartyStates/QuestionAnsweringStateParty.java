package roboy.dialog.states.multiPartyStates;

import roboy.dialog.states.definitions.State;
import roboy.linguistics.sentenceanalysis.Interpretation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.context.Context;
import roboy.context.contextObjects.IntentValue;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.Linguistics;
import roboy.linguistics.Triple;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static roboy.memory.Neo4jRelationship.*;

public class QuestionAnsweringStateParty extends State {

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

    private int speakerCount;

    /**
     * Create a state object with given identifier (state name) and parameters.
     * <p>
     * The parameters should contain a reference to a state machine for later use.
     * The state will not automatically add itself to the state machine.
     *
     * @param stateIdentifier identifier (name) of this state
     * @param params          parameters for this state, should contain a reference to a state machine
     */
    public QuestionAnsweringStateParty(String stateIdentifier, StateParameters params) {
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
        return null;
    }

    @Override
    public Output react(ArrayList<Interpretation> input) {

        speakerCount = input.get(0).getSpeakerInfo().getSpeakerCount();
        for(int i=0; i<speakerCount; i++){

            if(roboySuggestedGame && getInference().inferSentiment(input.get(i)) == Linguistics.UtteranceSentiment.POSITIVE){
                roboySuggestedGame = false;
                userWantsGame = true;
                return Output.say(Verbalizer.startSomething.getRandomElement());
            }

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

    @Override
    public State getNextState() {
        return null;
    }

    /**
     * React to answer of the specifying question asked previously.
     *
     * @param input something like "yes" or "no"
     * @return answer to the answer to the original question if specifying question was answered with 'yes'
     */
    private Output reactToSpecifyingAnswer(ArrayList<Interpretation> input) {

        askingSpecifyingQuestion = false;

        for(int i=0; i<speakerCount; i++){
            // check if answer is yes
            if (input.get(i).getSentence() != null && input.get(i).getSentence().contains("yes")) {
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
        //TODO noooooooo
        return null;
    }

    private Output reactToQuestion(ArrayList<Interpretation> input) {

        askingSpecifyingQuestion = false;
        questionsAnswered++;

        Map<Integer, Interlocutor> persons = getContext().ACTIVE_INTERLOCUTORS.getValue();


        for(int i=0; i<speakerCount; i++){
            String name = persons.get(i).getName();
            Linguistics.ParsingOutcome parseOutcome = input.get(i).getParsingOutcome();
            if (parseOutcome == null) {
                logger.error("Invalid parser outcome!");
                return Output.say("Invalid parser outcome!");
            }

            if (parseOutcome == Linguistics.ParsingOutcome.UNDERSPECIFIED) {

                // ambiguous question, luckily the parser has prepared a followup question
                // and maybe even an answer if we are lucky (we will check in reactToSpecifyingAnswer later)

                String question = input.get(i).getUnderspecifiedQuestion();
                answerAfterUnspecifiedQuestion = input.get(i).getAnswer(); // could be null, but that's fine for now

                askingSpecifyingQuestion = true; // next input should be a yes/no answer


                return Output.say(name + " Could you be more precise, please? " + question);
            }
            //TODO this is really not nice ... find a way to concatinate the returns for all speakers

            if (parseOutcome == Linguistics.ParsingOutcome.SUCCESS) {
                if (input.get(i).getAnswer() != null) {
                    // tell the answer, that was provided by the parser
                    return Output.say(name + " " + answerStartingPhrases.getRandomElement() + " " + input.get(i).getAnswer());

                } else {
                    // check for triple


                    // parser could parse the question but has no answer
                    //TODO ah nicht schön
                    return useMemoryOrFallback(input);
                }
            }
        }

        // from here we know that dummyParserResult.equals("FAILURE")
        return useMemoryOrFallback(input);
    }


    private Output useMemoryOrFallback(ArrayList<Interpretation> input) {
        for(int i=0; i<speakerCount; i++){
            try {
                if (input.get(i).getSemTriples() != null) {
                    Output memoryAnswer = answerFromMemory(input.get(i).getSemTriples());
                    if (memoryAnswer != null) return memoryAnswer;
                }
            } catch (Exception e) {
                logger.warn(e.getMessage());
            }
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


    private boolean userWantsGameCheck(ArrayList<Interpretation> input){

        userWantsGame = false;
        for(int i=0; i<speakerCount; i++){
            List<String> tokens = input.get(i).getTokens();
            if (tokens != null && !tokens.isEmpty()){
                if(tokens.contains("game") || tokens.contains("play") || tokens.contains("games")){
                    userWantsGame = true;
                }
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



}


