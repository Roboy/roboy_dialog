package roboy.dialog.states;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.context.Context;
import roboy.linguistics.Linguistics;
import roboy.linguistics.Triple;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.Neo4jRelationships;
import roboy.memory.nodes.Interlocutor;
import roboy.memory.nodes.Interlocutor.RelationshipAvailability;
import static roboy.memory.nodes.Interlocutor.RelationshipAvailability.*;
import roboy.memory.nodes.MemoryNodeModel;
import roboy.dialog.Segue;
import roboy.util.RandomList;

import java.util.List;
import java.util.Set;

import static roboy.memory.Neo4jRelationships.*;


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
    private final String UPDATE_KNOWN_PERSON = "knownPerson";
    private final String LEARN_ABOUT_PERSON = "newPerson";
    private final Logger logger = LogManager.getLogger();

    private Neo4jRelationships[] predicates = { FROM, HAS_HOBBY, WORK_FOR, STUDY_AT };
    private State nextState;

    public IntroductionState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

    @Override
    public Output act() {
        // TODO: add more different introduction phrases
        return Output.say("I'm Roboy. What's your name?");
    }

    @Override
    public Output react(Interpretation input) {

        // expecting something like "I'm NAME"

        // 1. get name
        String name = getNameFromInput(input);

        if (name == null) {
            // input couldn't be parsed properly
            // TODO: do something intelligent if the parser fails
            nextState = this;
            logger.warn("IntroductionState couldn't get name! Staying in the same state.");
            return Output.say("Sorry, my parser is out of service.");
            // alternatively: Output.useFallback() or Output.sayNothing()
        }


        // 2. get interlocutor object from context
        // this also should query memory and do other magic
        Interlocutor person = Context.getInstance().ACTIVE_INTERLOCUTOR.getValue();
        person.addName(name);


        // 3. update interlocutor in context
        updateInterlocutorInContext(person);

        // 4. check if person is known/familiar
        if (person.FAMILIAR) {

            // 4a. person known/familiar
            String retrievedResult = "";
            RandomList<MemoryNodeModel> nodes = retrieveNodesFromMemoryByIds(person.getRelationships(Neo4jRelationships.FRIEND_OF));
            if (!nodes.isEmpty()) {
                retrievedResult = " You are friends with " + nodes.getRandomElement().getProperties().get("name").toString();
            }

            RelationshipAvailability availability = person.checkRelationshipAvailability(predicates);
            if (availability == SOME_AVAILABLE) {
                nextState = (Math.random() < 0.3) ? getTransition(UPDATE_KNOWN_PERSON) : getTransition(LEARN_ABOUT_PERSON);
            } else if (availability == NONE_AVAILABLE) {
                nextState = getTransition(LEARN_ABOUT_PERSON);
            } else {
                nextState = getTransition(UPDATE_KNOWN_PERSON);
            }

            // TODO: get some friends or hobbies of the person to make answer more interesting
            return Output.say("Hey, I know you, " + person.getName() + retrievedResult);

        } else {
            // 4b. person is not known
            nextState = getTransition(LEARN_ABOUT_PERSON);

            // TODO: what would you say to a new person?
            return Output.say(String.format("Nice to meet you, %s!", name)).setSegue(new Segue(Segue.SegueType.DISTRACT, 0.6));
        }
    }

    @Override
    public State getNextState() {
        return nextState;
    }

    private String getNameFromInput(Interpretation input) {
        // TODO: call Emilka's parser
        if (input.getSentenceType().compareTo(Linguistics.SENTENCE_TYPE.STATEMENT) == 0) {
            String[] tokens = (String[]) input.getFeatures().get(Linguistics.TOKENS);
            if (tokens.length == 1) {
                return tokens[0].replace("[", "").replace("]","").toLowerCase();
            } else {
                if (input.getFeatures().get(Linguistics.PARSER_RESULT).toString().equals("SUCCESS")) {
                    List<Triple> result = (List<Triple>) input.getFeatures().get(Linguistics.SEM_TRIPLE);
                    if (result.size() != 0) {
                        return result.get(0).object.toLowerCase();
                    } else {
                        if (input.getFeatures().get(Linguistics.OBJ_ANSWER) != null) {
                            String name = input.getFeatures().get(Linguistics.OBJ_ANSWER).toString().toLowerCase();
                            return !name.equals("") ? name : null;
                        }
                    }
                } else {
                    if (input.getFeatures().get(Linguistics.OBJ_ANSWER) != null) {
                        String name = input.getFeatures().get(Linguistics.OBJ_ANSWER).toString().toLowerCase();
                        return !name.equals("") ? name : null;
                    }
                }
            }
        }
        return null;
    }

    private void updateInterlocutorInContext(Interlocutor interlocutor) {
        Context.getInstance().ACTIVE_INTERLOCUTOR_UPDATER.updateValue(interlocutor);
    }


    @Override
    protected Set<String> getRequiredTransitionNames() {
        return newSet(UPDATE_KNOWN_PERSON, LEARN_ABOUT_PERSON);
    }
}
