package roboy.newDialog.states;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.context.Context;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.Neo4jRelationships;
import roboy.memory.nodes.Interlocutor;
import roboy.memory.nodes.MemoryNodeModel;
import roboy.memory.Neo4jMemory;

import java.io.IOException;
import java.util.ArrayList;
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
 * TODO: maybe react() would like to have it?
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
        name = "laura"; // TODO: Remove
        if (name == null) {
            // input couldn't be parsed properly
            // TODO: do something intelligent if the parser fails
            nextState = this;
            logger.warn("IntroductionState couldn't get name! Staying in the same state.");
            return Output.say("Sorry, my parser is out of service.");
            // alternatively: Output.useFallback() or Output.sayNothing()
        }


        // 2. create interlocutor object based on the person name
        // this also should query memory and do other magic
        Interlocutor person = new Interlocutor();
        person.addName(name);


        // 3. update interlocutor in context
        updateInterlocutorInContext(person);

        // 4. check if person is known/familiar
        if (person.FAMILIAR) {

            // 4a. person known/familiar
            String retrievedResult = "";
            ArrayList<Integer> ids = person.getRelationships(Neo4jRelationships.FRIEND_OF);
            if (ids != null && !ids.isEmpty()) {
                Neo4jMemory memory = Neo4jMemory.getInstance();
                try {
                    MemoryNodeModel requestedObject = memory.getById(ids.get(ids.size()-1));
                    retrievedResult = requestedObject.getProperties().get("name").toString();
                } catch (InterruptedException | IOException e) {
                    logger.error("Error on Memory data retrieval: " + e.getMessage());
                }
            }

            Boolean infoPurity = checkInfoPurity3VL(person);
            if (infoPurity == null) {
                nextState = (Math.random() < 0.3) ? getTransition(UPDATE_KNOWN_PERSON) : getTransition(LEARN_ABOUT_PERSON);
            } else {
                nextState = infoPurity ? getTransition(UPDATE_KNOWN_PERSON) : getTransition(LEARN_ABOUT_PERSON);
            }

            // TODO: get some friends or hobbies of the person to make answer more interesting
            return Output.say("Hey, I know you, " + person.getName() + "! You are friends with " + retrievedResult);

        } else {
            // 4b. person is not known
            nextState = getTransition(LEARN_ABOUT_PERSON);

            // TODO: what would you say to a new person?
            return Output.say("Nice to meet you!");
        }
    }

    @Override
    public State getNextState() {
        return nextState;
    }

    private String getNameFromInput(Interpretation input) {
        // TODO: call Emilka's parser
        return null;
    }

    private void updateInterlocutorInContext(Interlocutor interlocutor) {
        Context.getInstance().ACTIVE_INTERLOCUTOR_UPDATER.updateValue(interlocutor);
    }

    private Boolean checkInfoPurity3VL(Interlocutor interlocutor) {
        List<Boolean> personInfoPurity = new List<Boolean>;

        for (Neo4jRelationships predicate : predicates) {
            personInfoPurity.add(interlocutor.hasRelationship(predicate));
        }

        if (personInfoPurity.contains(true)) {
            if (personInfoPurity.contains(false)) {
                return null;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }


    @Override
    protected Set<String> getRequiredTransitionNames() {
        return newSet(UPDATE_KNOWN_PERSON, LEARN_ABOUT_PERSON);
    }
}
