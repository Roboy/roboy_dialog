package roboy.newDialog.examples.toyStates;

import roboy.newDialog.states.State;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.newDialog.states.StateParameters;

/**
 * ToyFarewellState always acts with "Bye bye" to indicate the end of conversation.
 * The interlocutor's answer is ignored and there is no reaction (Output.sayNothing()).
 * This ends the conversation (returning null in getNextState()).
 *
 * ToyFarewellState interface:
 * 1) Fallback is not required.
 * 2) This state has no outgoing transitions.
 * 3) No parameters are used.
 */
public class ToyFarewellState extends State {

    public ToyFarewellState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

    @Override
    public Output act() {
        return Output.say("I hereby declare on oath, that I absolutely and entirely " +
                "renounce and abjure allegiance and fidelity to any prince, potentate, " +
                "state, or sovereignty, of whom or which I have therefore been a subject " +
                "or citizen; that I will support and defend the Constitution of the Republic " +
                "of Uzupis against all enemies, foreign and domestic; that I will bear true faith " +
                "and allegiance to the same; and that I take this obligation freely, without any " +
                "mental reservation of purpose of evasion, so help me Mother Earth and Father Sky.");
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
