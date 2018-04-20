package roboy.dialog.states.expoStates;

import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.sentenceanalysis.Interpretation;

/**
 * This state will:
 * - ask the interlocutor for his name
 * - create and update the interlocutor in the context
 * - take one transition: roboyInfo
 *
 * ExpoIntroductionState interface:
 * 1) Fallback is not required.
 * 2) Outgoing transitions that have to be defined:
 *    - roboyInfo:    following state if the name was given
 * 3) No parameters are used.
 */
public class ExpoIntroductionState extends State {

    public ExpoIntroductionState(String stateIdentifier, StateParameters params) {
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
