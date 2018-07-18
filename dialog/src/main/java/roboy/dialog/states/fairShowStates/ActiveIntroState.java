package roboy.dialog.states.fairShowStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.nodes.Roboy;
import roboy.talk.Verbalizer;

/**
 * Passive state to start a conversation.
 * Roboy is introducing himself autonomously
 *
 */
public class ActiveIntroState extends State {

    private final static String TRANSITION_PEOPLE_AROUND = "peopleAround";
    private final static String TRANSITION_LONELY_ROBOY = "lonelyRoboy";

    private final Logger LOGGER = LogManager.getLogger();

    private State nextState;

    public ActiveIntroState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

    @Override
    public Output act() {
        nextState = getTransition(TRANSITION_LONELY_ROBOY);
        return Output.say(Verbalizer.greetings.getRandomElement());
    }

    @Override
    public Output react(Interpretation input) {

        return Output.sayNothing();
    }

    @Override
    public State getNextState() {
        return nextState;
    }

}
