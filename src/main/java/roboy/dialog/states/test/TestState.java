package roboy.dialog.states.test;

import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.dialog.states.State;
import roboy.dialog.states.StateParameters;

import java.util.Set;

/**
 * Test state used for routing.
 */
public class TestState extends State {

    public TestState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

    @Override
    public Output act() {
        return Output.say("Entered test state");
    }

    @Override
    public Output react(Interpretation input) {
        return Output.say("Exited test state");
    }

    @Override
    public State getNextState() {
        return getTransition("next");
    }

    @Override
    protected Set<String> getRequiredTransitionNames() {
        return newSet("next");
    }
}
