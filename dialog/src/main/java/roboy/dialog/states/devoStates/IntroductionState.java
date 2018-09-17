package roboy.dialog.states.devoStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.dialog.Segue;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.Neo4jProperty;
import roboy.memory.Neo4jRelationship;
import roboy.memory.nodes.Interlocutor;
import roboy.memory.nodes.Interlocutor.RelationshipAvailability;
import roboy.memory.nodes.MemoryNodeModel;
import roboy.memory.nodes.Roboy;
import roboy.util.Agedater;
import roboy.util.QAJsonParser;
import roboy.util.RandomList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import static roboy.memory.Neo4jProperty.*;
import static roboy.memory.Neo4jRelationship.*;
import static roboy.memory.nodes.Interlocutor.RelationshipAvailability.NONE_AVAILABLE;
import static roboy.memory.nodes.Interlocutor.RelationshipAvailability.SOME_AVAILABLE;

/**
 * This state will:
 * - ask the interlocutor for his name
 * - query memory if the person is already known
 * - create and update the interlocutor in the context
 * - take one of two transitions: knownPerson or newPerson
 *
 * IntroductionState interface:
 * 1) Fallback is not required.
 * 2) Outgoing transitions that have to be defined:
 *    - knownPerson:    following state if the person is already known
 *    - newPerson:      following state if the person is NOT known
 * 3) No parameters are used.
 */
public class IntroductionState extends State {
    private QAJsonParser infoValues;
    private final String UPDATE_KNOWN_PERSON = "knownPerson";
    private final String LEARN_ABOUT_PERSON = "newPerson";
    private final Logger LOGGER = LogManager.getLogger();
    private final String INFO_FILE_PARAMETER_ID = "infoFile";
    private final RandomList<String> introPhrases = new RandomList<>("What's your name?");
    private final RandomList<String> successResponsePhrases = new RandomList<>("Hey, I know you, %s!");
    private final RandomList<String> failureResponsePhrases = new RandomList<>("Nice to meet you, %s!");

    private Neo4jRelationship[] personPredicates = { FROM, HAS_HOBBY, WORK_FOR, STUDY_AT };
    private RandomList<Neo4jRelationship> roboyRelatioshipPredicates = new RandomList<>(FROM, MEMBER_OF, LIVE_IN, HAS_HOBBY, FRIEND_OF, CHILD_OF, SIBLING_OF);
    private RandomList<Neo4jProperty> roboyPropertiesPredicates = new RandomList<>(skills, abilities, future);
    private State nextState;

    public IntroductionState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
        String infoListPath = params.getParameter(INFO_FILE_PARAMETER_ID);
        LOGGER.info(" -> The infoList path: " + infoListPath);
        infoValues = new QAJsonParser(infoListPath);
    }

    @Override
    public Output act() {
        return Output.say(getIntroPhrase());
    }

    @Override
    public Output react(Interpretation input) {
        // expecting something like "My name is NAME"

        // 1. get name
        String name = getNameFromInput(input);

        if (name == null) {
            // input couldn't be parsed properly
            // TODO: do something intelligent if the parser fails
            nextState = this;
            LOGGER.warn("IntroductionState couldn't get name! Staying in the same state.");
            return Output.say("Sorry, my parser is out of service.");
            // alternatively: Output.useFallback() or Output.sayNothing()
        }


        // 2. get interlocutor object from context
        // this also should query memory and do other magic
        Interlocutor person = getContext().ACTIVE_INTERLOCUTOR.getValue();
        person.addName(name);
        // Roboy roboy = new Roboy(getMemory());


        // 3. update interlocutor in context
        updateInterlocutorInContext(person);
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

            RelationshipAvailability availability = person.checkRelationshipAvailability(personPredicates);
            if (availability == SOME_AVAILABLE) {
                nextState = (Math.random() < 0.3) ? getTransition(UPDATE_KNOWN_PERSON) : getTransition(LEARN_ABOUT_PERSON);
            } else if (availability == NONE_AVAILABLE) {
                nextState = getTransition(LEARN_ABOUT_PERSON);
            } else {
                nextState = getTransition(UPDATE_KNOWN_PERSON);
            }
        } else {
            // 4b. person is not known
            nextState = getTransition(LEARN_ABOUT_PERSON);
            segueProbability = 0.6;
        }
        String retrievedRoboyFacts = getRoboyFactsPhrase(new Roboy(getMemory()));
        Segue s = new Segue(Segue.SegueType.DISTRACT, segueProbability);
        return Output.say(getResponsePhrase(person.getName(), person.FAMILIAR) +
                retrievedPersonalFact + retrievedRoboyFacts).setSegue(s);
    }

    @Override
    public State getNextState() {
        return nextState;
    }

    private String getNameFromInput(Interpretation input) {
        return getInference().inferProperty(name, input);
    }

    private void updateInterlocutorInContext(Interlocutor interlocutor) {
        getContext().ACTIVE_INTERLOCUTOR_UPDATER.updateValue(interlocutor);
    }

    private String getIntroPhrase() {
        return introPhrases.getRandomElement();
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
    protected Set<String> getRequiredTransitionNames() {
        return newSet(UPDATE_KNOWN_PERSON, LEARN_ABOUT_PERSON);
    }
}
