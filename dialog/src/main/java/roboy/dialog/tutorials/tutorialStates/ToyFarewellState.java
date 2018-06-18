package roboy.dialog.tutorials.tutorialStates;

import roboy.dialog.states.definitions.State;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.dialog.states.definitions.StateParameters;

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
        // Two options here:
        // - say "bye bye", ignore response and end conversation after react():
        // - or say "bye bye" and immediately end the conversation

        // wait for response
        //return Output.say( "Bye bye! [say anything, will end conversation]" );

        // end immediately
        return Output.endConversation("Bye bye! [end conversation immediately]");

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
