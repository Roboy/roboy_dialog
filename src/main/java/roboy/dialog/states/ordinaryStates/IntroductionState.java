package roboy.dialog.states.ordinaryStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.context.Context;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.Linguistics;
import roboy.linguistics.Triple;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.Neo4jProperties;
import roboy.memory.Neo4jRelationships;
import roboy.memory.nodes.Interlocutor;
import roboy.memory.nodes.Interlocutor.RelationshipAvailability;
import roboy.memory.nodes.Roboy;
import roboy.memory.nodes.MemoryNodeModel;
import roboy.dialog.Segue;
import roboy.util.QAJsonParser;
import roboy.util.RandomList;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

import static roboy.memory.Neo4jRelationships.*;
import static roboy.memory.Neo4jProperties.*;
import static roboy.memory.nodes.Interlocutor.RelationshipAvailability.*;

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

    private Neo4jRelationships[] personPredicates = { FROM, HAS_HOBBY, WORK_FOR, STUDY_AT };
    private RandomList<Neo4jRelationships> roboyRelatioshipPredicates = new RandomList<>(FROM, MEMBER_OF, LIVE_IN, HAS_HOBBY, FRIEND_OF, CHILD_OF, SIBLING_OF);
    private RandomList<Neo4jProperties> roboyPropertiesPredicates = new RandomList<>(skills, abilities, future);
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
        Interlocutor person = Context.getInstance().ACTIVE_INTERLOCUTOR.getValue();
        person.addName(name);
        Roboy roboy = new Roboy(getMemory());


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
                        nodes.getRandomElement().getProperties().get("name").toString();
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
        String result = null;
        if (input.getSentenceType().compareTo(Linguistics.SENTENCE_TYPE.STATEMENT) == 0) {
            String[] tokens = (String[]) input.getFeatures().get(Linguistics.TOKENS);
            if (tokens.length == 1) {
                result =  tokens[0].replace("[", "").replace("]","").toLowerCase();
                LOGGER.info(" -> Retrieved only one token: " + result);
                return result;
            } else {
                if (input.getFeatures().get(Linguistics.PARSER_RESULT).toString().equals("SUCCESS") &&
                        ((List<Triple>) input.getFeatures().get(Linguistics.SEM_TRIPLE)).size() != 0) {
                    LOGGER.info(" -> Semantic parsing is successful and semantic triple exists");
                    List<Triple> triple = (List<Triple>) input.getFeatures().get(Linguistics.SEM_TRIPLE);
                    result = triple.get(0).object.toLowerCase();
                    LOGGER.info(" -> Retrieved object " + result);
                } else {
                    LOGGER.warn(" -> Semantic parsing failed or semantic triple does not exist");
                    if (input.getFeatures().get(Linguistics.OBJ_ANSWER) != null) {
                        LOGGER.info(" -> OBJ_ANSWER exits");
                        String name = input.getFeatures().get(Linguistics.OBJ_ANSWER).toString().toLowerCase();
                        if (!name.equals("")) {
                            result = name;
                            LOGGER.info(" -> Retrieved OBJ_ANSWER result " + result);
                        } else {
                            LOGGER.warn(" -> OBJ_ANSWER is empty");
                        }
                    } else {
                        LOGGER.warn(" -> OBJ_ANSWER does not exit");
                    }
                }
            }
        }
        return result;
    }

    private void updateInterlocutorInContext(Interlocutor interlocutor) {
        Context.getInstance().ACTIVE_INTERLOCUTOR_UPDATER.updateValue(interlocutor);
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
            HashMap<String, Object> properties = roboy.getProperties();
            if (properties.containsKey(full_name.type)) {
                result += " " + String.format(infoValues.getSuccessAnswers(full_name).getRandomElement(), properties.get(full_name.type));
            }
            if (properties.containsKey(birthdate.type)) {
                result += " " + String.format(infoValues.getSuccessAnswers(age).getRandomElement(), determineAge(properties.get(birthdate.type).toString()));
            } else if (properties.containsKey(age.type)) {
                result += " " + String.format(infoValues.getSuccessAnswers(age).getRandomElement(), properties.get(age.type) + " years!");
            }
            if (properties.containsKey(skills.type)) {
                RandomList<String> retrievedResult = new RandomList<>(Arrays.asList(properties.get("skills").toString().split(",")));
                result += " " + String.format(infoValues.getSuccessAnswers(skills).getRandomElement(), retrievedResult.getRandomElement());
            }
            if (properties.containsKey(abilities.type)) {
                RandomList<String> retrievedResult = new RandomList<>(Arrays.asList(properties.get("abilities").toString().split(",")));
                result += " " + String.format(infoValues.getSuccessAnswers(abilities).getRandomElement(), retrievedResult.getRandomElement());
            }
            if (properties.containsKey(future.type)) {
                RandomList<String> retrievedResult = new RandomList<>(Arrays.asList(properties.get("future").toString().split(",")));
                result += " " + String.format(infoValues.getSuccessAnswers(future).getRandomElement(), retrievedResult.getRandomElement());
            }
        }

        if (result.equals("")) {
            result = "I am Roboy 2.0! ";
        }

        // Get a random relationship fact
        Neo4jRelationships predicate = roboyRelatioshipPredicates.getRandomElement();
        MemoryNodeModel node = getMemNodesByIds(roboy.getRelationships(predicate)).getRandomElement();
        if (node != null) {
            String nodeName = "";
            if (node.getProperties().containsKey("full_name") && !node.getProperties().get("full_name").equals("")) {
                nodeName = node.getProperties().get("full_name").toString();
            } else {
                nodeName = node.getProperties().get("name").toString();
            }
            result += " " + String.format(infoValues.getSuccessAnswers(predicate).getRandomElement(), nodeName);
        }

        return result;
    }

    /**
     * A helper function to determine the age based on the birthdate
     *
     * Java 8 specific
     *
     * @param datestring
     * @return
     */
    private String determineAge(String datestring) {
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
        Date date = null;
        try {
            date = format.parse(datestring);
        } catch (ParseException e) {
            LOGGER.error("Error while parsing a date: " + datestring + ". " + e.getMessage());
        }
        if (date != null) {
            LocalDate birthdate = date.toInstant().atZone(ZoneId.of("Europe/Berlin")).toLocalDate();
            LocalDate now = LocalDate.now(ZoneId.of("Europe/Berlin"));
            Period age = Period.between(birthdate, now);
            int years = age.getYears();
            int months = age.getMonths();
            int days = age.getDays();
            if (years > 0) {
                return years + " years";
            } else if (months > 0) {
                return months + " months";
            } else {
                return days + " days";
            }
        } else {
            return "0 years";
        }
    }

    @Override
    protected Set<String> getRequiredTransitionNames() {
        return newSet(UPDATE_KNOWN_PERSON, LEARN_ABOUT_PERSON);
    }
}
