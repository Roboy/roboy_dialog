package roboy.dialog.states.definitions;


import roboy.linguistics.sentenceanalysis.Interpretation;

public abstract class MonologState extends State {

    /**
     * Create a state object with given identifier (state name) and parameters.
     * <p>
     * The parameters should contain a reference to a state machine for later use.
     * The state will not automatically add itself to the state machine.
     *
     * @param stateIdentifier identifier (name) of this state
     * @param params          parameters for this state, should contain a reference to a state machine
     */
    public MonologState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }


    /**
     * Defines how to react to an input. This is usually the answer to the incoming question or some other statement.
     * If this state can't react, it can return 'null' to trigger the fallback state for the answer.
     *
     * Note: In the new architecture, react() does not define the next state anymore! Reaction and state
     * transitions are now decoupled. State transitions are defined in getNextState()
     *
     * @param input input from the person we talk to
     * @return reaction to the input (should not be null)
     */
    @Override
    public Output react(Interpretation input){

        return Output.skipInput();
    }


}
