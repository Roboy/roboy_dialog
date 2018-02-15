package roboy.newDialog.states;

import roboy.linguistics.sentenceanalysis.Interpretation;


/**
 * This state will:
 * - ask the interlocutor for his name
 * - query memory if the person is already known
 * - create and update the interlocutor in the context
 * - take one of two transitions: knownPerson or newPerson
 *
 * IntroductionState interface:
 * 1) Fallback is not required.
 * 2) Outgoing transitions that have to be defined:
 *    - knownPerson:    following state if the person is already known
 *    - newPerson:      following state if the person is NOT known
 * 3) No parameters are used.
 */
public class IntroductionState extends State {

    public IntroductionState(String stateIdentifier, StateParameters params) {
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
