package roboy.dialog.states.devoStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.context.contextObjects.IntentValue;
import roboy.dialog.Segue;
import roboy.dialog.states.definitions.ExpoState;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.dialog.states.ordinaryStates.PersonalInformationFollowUpState;
import roboy.emotions.RoboyEmotion;
import roboy.linguistics.Linguistics;
import roboy.linguistics.Linguistics.SemanticRole;
import roboy.linguistics.Triple;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.Neo4jRelationship;
import roboy.memory.Neo4jProperty;
import roboy.memory.nodes.Interlocutor;
import roboy.memory.nodes.MemoryNodeModel;
import roboy.memory.nodes.Roboy;
import roboy.talk.PhraseCollection;
import roboy.talk.Verbalizer;
import roboy.util.Agedater;
import roboy.util.Pair;
import roboy.util.QAJsonParser;
import roboy.util.RandomList;
import roboy.util.api.*;

import java.util.*;

import static roboy.memory.Neo4jProperty.full_name;
import static roboy.memory.Neo4jProperty.name;
import static roboy.memory.Neo4jRelationship.*;
import static roboy.memory.nodes.Interlocutor.RelationshipAvailability.NONE_AVAILABLE;
import static roboy.memory.nodes.Interlocutor.RelationshipAvailability.SOME_AVAILABLE;

/**
 * QuestionAnsweringState
 * Roboy Question Answering State
 *
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
public class QuestionRoboyQAState extends ExpoState {
    private final Logger LOGGER = LogManager.getLogger();

    private final static String TRANSITION_FINISHED_ANSWERING = "finishedQuestionAnswering";
    private final static String TRANSITION_LOOP_TO_NEW_PERSON = "loopToNewPerson";
    private final static String TRANSITION_LOOP_TO_KNOWN_PERSON = "loopToKnownPerson";
    private final static String TRANSITION_SWITCH_TO_GAMING = "switchToGaming";
    private final static int MAX_NUM_OF_QUESTIONS = 5;
    private int questionsAnswered = 0;

    private final static RandomList<String> reenteringPhrases = PhraseCollection.QUESTION_ANSWERING_REENTERING;
    private final static RandomList<String> answerStartingPhrases = PhraseCollection.QUESTION_ANSWERING_START;
    private final String INFO_FILE_PARAMETER_ID = "infoFile";

    private boolean askingSpecifyingQuestion = false;
    private String answerAfterUnspecifiedQuestion = ""; // the answer to use if specifying question is answered with YES
    private boolean offeredGame = false;
    private boolean userWantsGame = false;

    private QAJsonParser infoValues;

    public QuestionRoboyQAState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
        String infoListPath = params.getParameter(INFO_FILE_PARAMETER_ID);
        LOGGER.info("The infoList path: " + infoListPath);
        infoValues = new QAJsonParser(infoListPath);
    }

    @Override
    public Output act() {
        if (Math.random() < 0.1) {
            offeredGame = true;
            return Output.say(PhraseCollection.OFFER_GAME_PHRASES.getRandomElement());
        }
        return Output.sayNothing();
    }

    @Override
    public Output react(Interpretation input) {
        return reactToQuestion(input);
    }

    private Output reactToQuestion(Interpretation input) {

        if (offeredGame) {
            questionsAnswered++;
            offeredGame = false;
            if (getInference().inferSentiment(input) == Linguistics.UtteranceSentiment.POSITIVE) {
                userWantsGame = true;
                questionsAnswered++;
                return Output.sayNothing().setSegue(new Segue(Segue.SegueType.PICKUP,0.5)).setEmotion(RoboyEmotion.HAPPY);
            }
            else {
                return Output.sayNothing().setEmotion(RoboyEmotion.SADNESS);
            }
        }

        askingSpecifyingQuestion = false;
        userWantsGame = false;
        questionsAnswered++;

        String answer = inferApiAnswer(input);
        if (!answer.isEmpty()) {
            return Output.say(answer);
        }

        // catch jokes
        if (input.getTokens().contains("joke") || input.getTokens().contains("funny")) {
            return Output.say(PhraseCollection.JOKES.getRandomElement()).setEmotion(RoboyEmotion.positive.getRandomElement());
        }

        if (input.getTokens() != null && !(input.getTokens().contains("you") || input.getTokens().contains("your"))) {
            Linguistics.ParsingOutcome parseOutcome = input.getParsingOutcome();

            if (parseOutcome == null) {
                LOGGER.error("Invalid parser outcome!");
                return Output.useFallback();
            }

            if (parseOutcome == Linguistics.ParsingOutcome.SUCCESS) {
                if (input.getAnswer() != null) {
                    // tell the answer, that was provided by the parser
                    return Output.say(answerStartingPhrases.getRandomElement() + " " + input.getAnswer());
                }
            }
            // use a random fact and avoid answer.
            if (input.getTokens().size() > 2 && input.isQuestion()) {
                Interpretation i = new Interpretation(PhraseCollection.FACTS.getRandomElement());
                i.setSentenceType(Linguistics.SentenceType.ANECDOTE);
                return Output.say(i);
            }

            return Output.useFallback();
        }

        // from here we know that dummyParserResult.equals("FAILURE")
        return useMemoryOrFallback(input);
    }

    @Override
    public State getNextState() {
        if (userWantsGame) {
            return getTransition(TRANSITION_SWITCH_TO_GAMING);
        }
        if (questionsAnswered > MAX_NUM_OF_QUESTIONS) { // enough questions answered --> finish asking
            questionsAnswered = 0;
            return getTransition(TRANSITION_FINISHED_ANSWERING);
        }
        return this;
    }

    private Output useMemoryOrFallback(Interpretation input) {
        try {
            if ( input.getPas() != null || input.getTriples() != null) {
                Output memoryAnswer = answerFromMemory(input);
                if (memoryAnswer != null) return memoryAnswer;
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage());
        }

        return Output.useFallback();
    }

    private Output answerFromMemory(Interpretation input) {

        // try to use memory to answer
        Roboy roboy = new Roboy(getMemory());

        //String answer = String.format(infoValues.getSuccessAnswers(predicate).getRandomElement(), inferMemoryAnswer(input, roboy));
        String answer = String.format("%s", inferMemoryAnswer(input, roboy));
        if (answer.equals("")) {
            return Output.useFallback();
        }
        return Output.say(answer);
    }

    private String inferApiAnswer(Interpretation input) {
        Map<SemanticRole, String> pas = input.getPas();
        String answer = "";
        if (matchPas(pas, new Pair(SemanticRole.PATIENT, ".*\\bweather\\b.*"))) {
            try {
                answer = String.format("It seems like it is %s out there!", Weather.getData("munich"));
            }
            catch (Exception e) {
                answer = "It seems a bit moody...";
                LOGGER.error(e.getMessage());
            }
        } else if (matchPas(pas, new Pair(SemanticRole.AGENT, ".*\\bmovie.*"))) {
            try {
                answer = String.format("I have heard that %s is playing!", Movie.getData("title"));
            }
            catch (Exception e) {
                answer = "Wall e is a great movie!";
                LOGGER.error(e.getMessage());
            }
        } else if (matchPas(pas, new Pair(SemanticRole.PATIENT, ".+ in .+"), new Pair(SemanticRole.MANNER, "how"), new Pair(SemanticRole.PREDICATE, "say"), new Pair(SemanticRole.AGENT, "you")) ||
                matchPas(pas, new Pair(SemanticRole.PATIENT, ".+ in .+"), new Pair(SemanticRole.PREDICATE, "is"), new Pair(SemanticRole.AGENT, "what"))) {
            String[] parts = pas.get(SemanticRole.PATIENT).split(" in ");
            assert(parts.length == 2);
            try {
                answer = answerStartingPhrases.getRandomElement() + " " + Translate.getData(parts[0], parts[1]);
            }
            catch (Exception e) {
                answer = String.format("I am not sure whether I know %s", parts[1]);
                LOGGER.error(e.getMessage());
            }
        }
        return answer;
    }

    private String inferMemoryAnswer(Interpretation input, Roboy roboy) {

        String answer = "";
        Map<SemanticRole, String> pas = input.getPas();
        List<Triple> triples = input.getTriples();

        if (pas != null) {
            answer = inferPasAnswer(pas, roboy);
            if (!answer.isEmpty()) {
                return answer;
            }
        }

        if (triples != null) {
            return inferTripleAnswer(triples, roboy);
        }

        if (input.getObjAnswer() != null) {
            String objAnswer = input.getObjAnswer().toLowerCase();
            if (!objAnswer.equals("")) {
                LOGGER.info("OBJ_ANSWER: " + objAnswer);
                Neo4jRelationship predicate = inferPredicateFromObjectAnswer(objAnswer);
                if (predicate != null) {
                    answer = extractNodeNameForPredicate(predicate, roboy);
                }
            } else {
                LOGGER.warn("OBJ_ANSWER is empty");
            }
        }

        return answer;
    }

    private String inferPasAnswer(Map<SemanticRole, String> pas, Roboy roboy) {
        String answer = "";
        if (matchPas(pas, new Pair(SemanticRole.AGENT, "old")) || matchPas(pas, new Pair(SemanticRole.PATIENT, ".*\\bage\\b.*"))) {
            answer = extractAge(roboy);
        } else if (matchPas(pas, new Pair(SemanticRole.PREDICATE, "from"))) {
            answer = extractNodeNameForPredicate(Neo4jRelationship.FROM, roboy);
        }
        else if (matchPas(pas, new Pair(SemanticRole.LOCATION, "where"), new Pair(SemanticRole.PREDICATE, "live"))) {
            answer = extractNodeNameForPredicate(Neo4jRelationship.LIVE_IN, roboy);
        }
//        else if (matchPas(pas, new Pair(SemanticRole.AGENT, "you"), new Pair(SemanticRole.MANNER, "how"))) {
//            answer = "Yo moma says I am a good boy!";
//        }
        else if (matchPas(pas, new Pair(SemanticRole.AGENT, "who"))) {
            if (matchPas(pas, new Pair(SemanticRole.PATIENT, "you"), new Pair(SemanticRole.PREDICATE, "are"))) {
                answer = extractNodeNameForPredicate(Neo4jProperty.name, roboy) + " "
                        + extractNodeNameForPredicate(Neo4jProperty.identitiy, roboy);
            } else if (matchPas(pas, new Pair(SemanticRole.PATIENT, ".*\\b(father|dad)\\b.*"))) {
                answer = extractNodeNameForPredicate(Neo4jRelationship.CHILD_OF, roboy);
            } else if (matchPas(pas, new Pair(SemanticRole.PATIENT, ".*\\b(sibling|brother)\\b.*"))){
                answer = extractNodeNameForPredicate(Neo4jRelationship.SIBLING_OF, roboy);
            } else if (matchPas(pas, new Pair(SemanticRole.PREDICATE, "created|made|built"), new Pair(SemanticRole.PATIENT, "you"))) {
                answer = extractNodeNameForPredicate(Neo4jRelationship.CREATED_BY, roboy);
            }
        } else if (matchPas(pas, new Pair(SemanticRole.PREDICATE, "do|like"), new Pair(SemanticRole.AGENT, "you"))){
//            double prob = Math.random();
//            if (prob < .3) {
//                answer = extractNodeNameForPredicate(Neo4jProperty.abilities, roboy);
//            } else if(prob < .7) {
//                answer = extractNodeNameForPredicate(Neo4jRelationship.HAS_HOBBY, roboy);
//            } else {
                answer = extractNodeNameForPredicate(Neo4jProperty.skills, roboy);
//            }
        } else if (matchPas(pas, new Pair(SemanticRole.PREDICATE, "dream|wish"), new Pair(SemanticRole.AGENT, "you"))) {
            answer = extractNodeNameForPredicate(Neo4jProperty.dreams, roboy);
        }
        else if (matchPas(pas, new Pair(SemanticRole.PATIENT, "you"), new Pair(SemanticRole.PREDICATE, "are"),
                new Pair(SemanticRole.AGENT, "what"))) {
            answer = extractNodeNameForPredicate(Neo4jProperty.name, roboy) + " "
                    + extractNodeNameForPredicate(Neo4jProperty.identitiy, roboy);
        }

        return answer;
    }

    private String inferTripleAnswer(List<Triple> triples, Roboy roboy) {
        String answer = "";
        for (Triple t: triples) {
            if (t.subject.matches(".*\\b(meet|see|know)\\b.*")) {
                answer = infoValues.getSuccessAnswers(Neo4jProperty.media).getRandomElement();
            }
        }
        // else if {OBJ: *} -> query * -> I'm sure I know a typeof(*) called *! (Where does he live? :))
        //
        // 	if * in Neo4jRelationship.FRIEND_OF
        return answer;
    }

    private Neo4jRelationship inferPredicateFromObjectAnswer(String objectAnswer) {
        if (objectAnswer.contains("hobby")) {
            return Neo4jRelationship.HAS_HOBBY;
        } else if (objectAnswer.contains("member")) {
            return Neo4jRelationship.MEMBER_OF;
        } else if (objectAnswer.contains("friend")) {
            return Neo4jRelationship.FRIEND_OF;
        } else if (objectAnswer.contains("where") ||
                objectAnswer.contains("city") ||
                objectAnswer.contains("place") ||
                objectAnswer.contains("country") ||
                objectAnswer.contains("live") ||
                objectAnswer.contains("life")) {
            return Neo4jRelationship.LIVE_IN;
        } else if (objectAnswer.contains("from") ||
                objectAnswer.contains("born")) {
            return Neo4jRelationship.FROM;
        } else if (objectAnswer.contains("child") ||
                objectAnswer.contains("father") ||
                objectAnswer.contains("dad") ||
                objectAnswer.contains("parent")) {
            return Neo4jRelationship.CHILD_OF;
        } else if (objectAnswer.contains("brother") ||
                objectAnswer.contains("family") ||
                objectAnswer.contains("relativ")) {
            return Neo4jRelationship.SIBLING_OF;
        }
        return null;
    }

    private String extractNodeNameForPredicate(Neo4jRelationship predicate, Roboy roboy) {
        MemoryNodeModel node = getMemNodesByIds(roboy.getRelationships(predicate)).getRandomElement();
        if (node != null) {
            String nodeName;
            if (node.getProperties().containsKey(full_name) && !node.getProperties().get(full_name).equals("")) {
                nodeName = node.getProperties().get(full_name).toString();
            } else {
                nodeName = node.getProperties().get(name).toString();
            }
            return String.format(infoValues.getSuccessAnswers(predicate).getRandomElement(), nodeName);
        }
        return null;
    }

    private String extractNodeNameForPredicate(Neo4jProperty predicate, Roboy roboy) {
        String property = roboy.getProperty(predicate).toString();
        if (property != null) {
            // check if there are multiple things in it
            RandomList<String> properties = new RandomList<>(property.split(","));
            //pick 2 random properties to talk about
            String propertiesToShare = properties.getRandomElement();
            properties.remove(propertiesToShare); // so we dont pick the same one again
            if (!properties.isEmpty()) {
                propertiesToShare += " and " + properties.getRandomElement();
            }
            return String.format(infoValues.getSuccessAnswers(predicate).getRandomElement(), propertiesToShare);
        }
        return null;
    }

    private String extractAge(Roboy roboy) {
        HashMap<String, Integer> ages = new Agedater().determineAge(roboy.getProperty(Neo4jProperty.birthdate).toString());
        String retrievedAge = "0 days";
        if (ages.get("years") > 0) {
            retrievedAge = ages.get("years") + " years";
        } else if (ages.get("months") > 0) {
            retrievedAge = ages.get("months") + " months";
        } else {
            retrievedAge = ages.get("days") + " days";
        }
        return String.format(infoValues.getSuccessAnswers(Neo4jProperty.age).getRandomElement(), retrievedAge);
    }

    private boolean matchPas(Map<SemanticRole, String> pas, Pair<SemanticRole, String>... matchCriteria) {
        if (pas == null)
            return false;
        boolean allCriteriaSatisfied = true;
        for (Pair<SemanticRole, String> criterion : matchCriteria) {
            if (!pas.containsKey(criterion.getKey()) ||
                    !pas.get(criterion.getKey()).matches("(?i)"+criterion.getValue())) { // (?i)-> case insesitive
                allCriteriaSatisfied = false;
                break;
            }
        }
        return allCriteriaSatisfied;
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