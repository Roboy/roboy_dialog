package roboy.dialog.states.monologStates;

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

    private final Logger LOGGER = LogManager.getLogger();

    private State nextState;

    private Roboy roboy;

    public IdleState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

    @Override
    public Output act() {

        return Output.say(roboy.getName());
    }

    @Override
    public Output react(Interpretation input) {

        return Output.sayNothing();
    }

    @Override
    public State getNextState() {
        return this;
    }

}
