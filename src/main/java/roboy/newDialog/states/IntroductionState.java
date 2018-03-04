package roboy.newDialog.states;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.nodes.Interlocutor;

import java.util.Set;


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
    private final String TRANSITION_KNOWN_PERSON = "knownPerson";
    private final String TRANSITION_NEW_PERSON = "newPerson";


    private final Logger logger = LogManager.getLogger();

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
            logger.warn("IntroductionState could't get name! Staying in the same state.");
            return Output.say("Sorry, my parser is broken.");
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
            nextState = getTransition(TRANSITION_KNOWN_PERSON);

            // TODO: get some friends or hobbies of the person to make answer more interesting
            return Output.say("Hey, I know you! + NAME here + some friends here");

        } else {

            // 4b. person is not known
            nextState = getTransition(TRANSITION_NEW_PERSON);

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
        // TODO: update interlocutor
    }


    @Override
    protected Set<String> getRequiredTransitionNames() {
        return newSet(TRANSITION_KNOWN_PERSON, TRANSITION_NEW_PERSON);
    }
}
