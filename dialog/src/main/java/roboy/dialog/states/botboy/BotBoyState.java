package roboy.dialog.states.botboy;

import org.apache.jena.atlas.logging.Log;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.dialog.Segue;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.emotions.RoboyEmotion;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.logic.StatementInterpreter;
import roboy.memory.Neo4jProperty;
import roboy.memory.Neo4jRelationship;
import roboy.memory.nodes.Interlocutor;
import roboy.memory.nodes.MemoryNodeModel;
import roboy.memory.nodes.Roboy;
import roboy.talk.Verbalizer;
import roboy.util.Agedater;
import roboy.util.QAJsonParser;
import roboy.util.RandomList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static roboy.memory.Neo4jProperty.*;
import static roboy.memory.Neo4jRelationship.*;
import static roboy.memory.Neo4jRelationship.SIBLING_OF;
import static roboy.memory.nodes.Interlocutor.RelationshipAvailability.NONE_AVAILABLE;
import static roboy.memory.nodes.Interlocutor.RelationshipAvailability.SOME_AVAILABLE;

/**
 * Start a conversation with telegram.
 * Try to detect a greeting or roboy names or some key word that initialize the conversation.
 */
public class BotBoyState extends State {

    private final Logger LOGGER = LogManager.getLogger();


    private QAJsonParser infoValues;
    private final String INFO_FILE_PARAMETER_ID = "infoFile";
    private Neo4jRelationship[] personPredicates = { FROM, HAS_HOBBY, WORK_FOR, STUDY_AT };
    private RandomList<Neo4jRelationship> roboyRelatioshipPredicates = new RandomList<>(FROM, MEMBER_OF, LIVE_IN, HAS_HOBBY, FRIEND_OF, CHILD_OF, SIBLING_OF);
    private final String UPDATE_KNOWN_PERSON = "knownPerson";
    private final String LEARN_ABOUT_PERSON = "newPerson";
    private final RandomList<String> successResponsePhrases = new RandomList<>("Hey, I know you, %s!");
    private final RandomList<String> failureResponsePhrases = new RandomList<>("Nice to meet you, %s!");

    private final String TRANSITION_GREETING_DETECTED = "greetingDetected";

    private State next;
    public BotBoyState(String stateIdentifiers, StateParameters params){
        super(stateIdentifiers, params);
        next = this;
        String infoListPath = params.getParameter(INFO_FILE_PARAMETER_ID);
        LOGGER.info(" -> The infoList path: " + infoListPath);
        infoValues = new QAJsonParser(infoListPath);
    }

    // first we wait for conversation to start
    @Override
    public Output act() {
        return Output.sayNothing();
    }


    @Override
    public Output react(Interpretation input) {
        Interlocutor person = getContext().ACTIVE_INTERLOCUTOR.getValue();
        String name = person.getName();
        if(name == null){
            String sentence = input.getSentence();

            boolean inputOK = StatementInterpreter.isFromList(sentence, Verbalizer.greetings) ||
                    StatementInterpreter.isFromList(sentence, Verbalizer.roboyNames) ||
                    StatementInterpreter.isFromList(sentence, Verbalizer.triggers);

            if(inputOK){
                next = getTransition(TRANSITION_GREETING_DETECTED);
                return Output.say("Hey! I was looking for someone to chat.").setEmotion(RoboyEmotion.HAPPINESS);
                //return Output.say(Verbalizer.greetings.getRandomElement());
            }

            return Output.sayNothing();
        }else{
            String retrievedPersonalFact = "";
            Double segueProbability = 0.0;

            // 4. check if person is known/familiar
            if (person.FAMILIAR) {
                // 4a. person known/familiar
                // TODO: get some hobbies or occupation of the person to make answer more interesting
                RandomList<MemoryNodeModel> nodes = getMemNodesByIds(person.getRelationships(FRIEND_OF));
                if (!nodes.isEmpty()) {
                    retrievedPersonalFact = " You are friends with " +
                            nodes.getRandomElement().getProperties().get(Neo4jProperty.name).toString();
                }

                Interlocutor.RelationshipAvailability availability = person.checkRelationshipAvailability(personPredicates);
                if (availability == SOME_AVAILABLE) {
                    next = (Math.random() < 0.3) ? getTransition(UPDATE_KNOWN_PERSON) : getTransition(LEARN_ABOUT_PERSON);
                } else if (availability == NONE_AVAILABLE) {
                    next = getTransition(LEARN_ABOUT_PERSON);
                } else {
                    next = getTransition(UPDATE_KNOWN_PERSON);
                }

                String retrievedRoboyFacts = getRoboyFactsPhrase(new Roboy(getMemory()));
                Segue s = new Segue(Segue.SegueType.DISTRACT, segueProbability);

                return Output.say(getResponsePhrase(person.getName(), person.FAMILIAR) + retrievedPersonalFact + " It is great to see you again!")
                        .setEmotion(RoboyEmotion.HAPPINESS)
                        .setSegue(s);
            } else {
                // 4b. person is not known
                next = getTransition(LEARN_ABOUT_PERSON);
                segueProbability = 0.6;

                String retrievedRoboyFacts = getRoboyFactsPhrase(new Roboy(getMemory()));
                Segue s = new Segue(Segue.SegueType.DISTRACT, segueProbability);

                return Output.say(getResponsePhrase(person.getName(), person.FAMILIAR) + retrievedPersonalFact + retrievedRoboyFacts)
                        .setEmotion(RoboyEmotion.HAPPINESS)
                        .setSegue(s);
            }
        }

    }
    private String getResponsePhrase(String name, boolean familiar) {
        if (familiar) {
            return String.format(successResponsePhrases.getRandomElement(), name);
        } else {
            return String.format(failureResponsePhrases.getRandomElement(), name);
        }
    }

    private String getRoboyFactsPhrase(Roboy roboy) {
        String result = "";

        // Get some random properties facts
        if (roboy.getProperties() != null && !roboy.getProperties().isEmpty()) {
            HashMap<Neo4jProperty, Object> properties = roboy.getProperties();
            if (properties.containsKey(full_name)) {
                result += " " + String.format(infoValues.getSuccessAnswers(full_name).getRandomElement(), properties.get(full_name));
            }
            if (properties.containsKey(birthdate)) {
                HashMap<String, Integer> ages = new Agedater().determineAge(properties.get(birthdate).toString());
                String retrievedAge = "0 days";
                if (ages.get("years") > 0) {
                    retrievedAge = ages.get("years") + " years";
                } else if (ages.get("months") > 0) {
                    retrievedAge = ages.get("months") + " months";
                } else {
                    retrievedAge = ages.get("days") + " days";
                }
                result += " " + String.format(infoValues.getSuccessAnswers(age).getRandomElement(), retrievedAge);
            } else if (properties.containsKey(age)) {
                result += " " + String.format(infoValues.getSuccessAnswers(age).getRandomElement(), properties.get(age) + " years!");
            }
            if (properties.containsKey(skills)) {
                RandomList<String> retrievedResult = new RandomList<>(Arrays.asList(properties.get(skills).toString().split(",")));
                result += " " + String.format(infoValues.getSuccessAnswers(skills).getRandomElement(), retrievedResult.getRandomElement());
            }
            if (properties.containsKey(abilities)) {
                RandomList<String> retrievedResult = new RandomList<>(Arrays.asList(properties.get(abilities).toString().split(",")));
                result += " " + String.format(infoValues.getSuccessAnswers(abilities).getRandomElement(), retrievedResult.getRandomElement());
            }
            if (properties.containsKey(future)) {
                RandomList<String> retrievedResult = new RandomList<>(Arrays.asList(properties.get(future).toString().split(",")));
                result += " " + String.format(infoValues.getSuccessAnswers(future).getRandomElement(), retrievedResult.getRandomElement());
            }
        }

        if (result.equals("")) {
            result = "I am Roboy 2.0! ";
        }

        // Get a random relationship fact
        Neo4jRelationship predicate = roboyRelatioshipPredicates.getRandomElement();
        MemoryNodeModel node = getMemNodesByIds(roboy.getRelationships(predicate)).getRandomElement();
        if (node != null) {
            String nodeName = "";
            if (node.getProperties().containsKey(full_name) && !node.getProperties().get(full_name).equals("")) {
                nodeName = node.getProperties().get(full_name).toString();
            } else {
                nodeName = node.getProperties().get(name).toString();
            }
            result += " " + String.format(infoValues.getSuccessAnswers(predicate).getRandomElement(), nodeName);
        }

        return result;
    }

    @Override
    public State getNextState() {
        return next;
    }

    @Override
    protected Set<String> getRequiredTransitionNames() {
        return newSet(TRANSITION_GREETING_DETECTED);
    }

    @Override
    protected Set<String> getRequiredParameterNames() {
        return newSet(); // empty set
    }

    @Override
    public boolean isFallbackRequired() {
        return false;
    }


}