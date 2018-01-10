package roboy.dialog.personality.experimental.toyStates;

import roboy.dialog.personality.experimental.AbstractState;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.util.Lists;

import java.util.*;

public class ToyIntroState extends AbstractState {


    public ToyIntroState(String stateIdentifier) {
        super(stateIdentifier);
    }

    @Override
    public List<Interpretation> act() {
        return Lists.interpretationList(new Interpretation("My name is Roboy! Who are you? [say anything]"));
    }

    @Override
    public List<Interpretation> react(Interpretation input) {
        return Lists.interpretationList(new Interpretation("Nice to meet you! [moving to next state]"));
    }

    @Override
    public AbstractState getNextState() {
        return getTransition("next");
    }

    @Override
    protected Set<String> getRequiredTransitionNames() {
        // optional: define all required transitions here:
        return newSet("next");
    }

}
