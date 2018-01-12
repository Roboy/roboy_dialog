package roboy.newDialog.states.toyStates;

import roboy.newDialog.states.State;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.util.Lists;

import java.util.List;

public class ToyFarewellState extends State {

    public ToyFarewellState(String stateIdentifier) {
        super(stateIdentifier);
    }

    @Override
    public List<Interpretation> act() {
        return Lists.interpretationList(new Interpretation("Bye bye! [say anything, will end conversation]"));
    }

    @Override
    public List<Interpretation> react(Interpretation input) {
        return null; // no reaction, we are done (fallback should also be set to null)
    }

    @Override
    public State getNextState() {
        return null; // no next state, we are done
    }

}
