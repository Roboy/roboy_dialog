package roboy.dialog.personality.experimental.toyStates;

import roboy.dialog.personality.experimental.AbstractState;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.util.Lists;

import java.util.List;

public class ToyFarewellState extends AbstractState {

    public ToyFarewellState(String stateIdentifier) {
        super(stateIdentifier);
    }

    @Override
    public List<Interpretation> act() {
        return Lists.interpretationList(new Interpretation(Linguistics.SENTENCE_TYPE.FAREWELL));
    }

    @Override
    public List<Interpretation> react(Interpretation input) {
        return null; // no reaction, we are done (fallback should also be set to null)
    }

    @Override
    public AbstractState getNextState() {
        return null; // no next state, we are done
    }

}
