package roboy.newDialog.states.toyStates;

import roboy.linguistics.Linguistics;
import roboy.newDialog.states.State;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.util.Lists;

import java.util.List;

/**
 * ToyFarewellState always acts with "Bye bye".
 * The Interlocutor answer is ignored and there is no reaction (empty interpretation).
 * This ends the conversation.
 *
 * Fallback is not required.
 * This state has no outgoing transitions.
 */
public class ToyFarewellState extends State {

    public ToyFarewellState(String stateIdentifier) {
        super(stateIdentifier);
    }

    @Override
    public Interpretation act() {
        return new Interpretation("Bye bye! [say anything, will end conversation]");
    }

    @Override
    public Interpretation react(Interpretation input) {
        // no reaction, we are done
        return new Interpretation(Linguistics.SENTENCE_TYPE.NONE);
    }

    @Override
    public State getNextState() {
        return null; // no next state, we are done
    }

}
