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

    /**
     * Can the game start?
     * @return True if game is capable of starting
     */
    public abstract boolean canStartGame();

    /**
     * What Roboy shall say to the user, explaining why he cannot start the given game, ie. Sorry, I need an internet connection to play World of Warcraft.
     * @return Output that contains reasoning
     */
    public abstract Output cannotStartWarning();

    /**
     * Tags that shall be used to infer, whether or not the user is specifically asking for a game. See {@link ChooseGameState}.infer method for how this is specifically implemented.
     * @return Collection of tags
     */
    public abstract Collection<String> getTags();

    /**
     * Get the Transition name, that is used to denote the GameState
     * @return Transition Name
     */
    public abstract String getTransitionName();
}
