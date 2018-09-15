package roboy.dialog.states.gameStates;

import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;

import java.util.Collection;

public abstract class GameState extends State {

    /**
     * Create a state object with given identifier (state name) and parameters.
     * <p>
     * The parameters should contain a reference to a state machine for later use.
     * The state will not automatically add itself to the state machine.
     *
     * @param stateIdentifier identifier (name) of this state
     * @param params          parameters for this state, should contain a reference to a state machine
     */
    public GameState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

    public abstract boolean canStartGame();

    public abstract Output cannotStartWarning();

    public abstract Collection<String> getTags();

    public abstract String getTransitionName();
}
