package roboy.newDialog.examples.toyStates;

import roboy.newDialog.states.State;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.newDialog.states.StateParameters;

/**
 * ToyFarewellState always acts with "Bye bye".
 * The Interlocutor's answer is ignored and there is no reaction (Output.sayNothing()).
 * This ends the conversation (returning null in getNextState()).
 *
 * Fallback is not required.
 * This state has no outgoing transitions.
 */
public class ToyFarewellState extends State {

    public ToyFarewellState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

    @Override
    public Output act() {
        return Output.say( new Interpretation("Bye bye! [say anything, will end conversation]") );
    }

    @Override
    public Output react(Interpretation input) {
        // no reaction, we are done
        return Output.sayNothing();
    }

    @Override
    public State getNextState() {
        return null; // no next state, we are done
    }

}
