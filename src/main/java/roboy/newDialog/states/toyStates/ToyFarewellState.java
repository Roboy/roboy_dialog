package roboy.newDialog.states.toyStates;

import roboy.linguistics.Linguistics;
import roboy.newDialog.states.State;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.newDialog.states.StateParameters;
import roboy.util.Lists;

import java.util.List;

/**
 * ToyFarewellState always acts with "Bye bye".
 * The Interlocutor's answer is ignored and there is no reaction (ReAct.sayNothing()).
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
    public ReAct act() {
        return ReAct.say( new Interpretation("Bye bye! [say anything, will end conversation]") );
    }

    @Override
    public ReAct react(Interpretation input) {
        // no reaction, we are done
        return ReAct.sayNothing();
    }

    @Override
    public State getNextState() {
        return null; // no next state, we are done
    }

}
