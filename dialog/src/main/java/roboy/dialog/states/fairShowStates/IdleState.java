package roboy.dialog.states.fairShowStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.nodes.Roboy;

/**
 * Idle state.
 * Roboy is waiting TBD minutes to autonomously start a conversation.
 *
 */
public class IdleState extends State {

    private final static String TRANSITION_TIME_IS_UP = "timeIsUp";

    private final Logger LOGGER = LogManager.getLogger();

    private State nextState;

    public IdleState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

    @Override
    public Output act() {
        nextState = getTransition(TRANSITION_TIME_IS_UP);
        return Output.say("I am in IdleState");
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
