package roboy.dialog.personality.experimental;

import roboy.dialog.action.Action;
import roboy.linguistics.sentenceanalysis.Interpretation;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * State machine for the personality.
 *
 */
public abstract class PersonalityStateMachine {

    // maps string identifiers to state objects ("Greeting" -> {GreetingState})
    // allows to have multiple instances of the same state class with different identifiers ("Greeting2" -> {GreetingState})
    private HashMap<String, AbstractState> indentifierToState;

    public PersonalityStateMachine() {
        indentifierToState = new HashMap<>();

    }


    public final void loadStateMachine(File f) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public final void saveStateMachine(File f) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public abstract List<Action> answer(Interpretation input);


}
