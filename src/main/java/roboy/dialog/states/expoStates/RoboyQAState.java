package roboy.dialog.states.expoStates;

import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.talk.PhraseCollection;
import roboy.util.RandomList;

public class RoboyQAState extends State {

    private final RandomList<String> roboyQAPhrases = PhraseCollection.INFO_ROBOY_INTENT_PHRASES;

    public RoboyQAState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

    @Override
    public Output act() {
        return null;
    }

    @Override
    public Output react(Interpretation input) {
        return null;
    }

    @Override
    public State getNextState() {
        return null;
    }
}
