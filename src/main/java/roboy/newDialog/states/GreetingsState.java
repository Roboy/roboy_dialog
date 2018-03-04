package roboy.newDialog.states;

import roboy.linguistics.sentenceanalysis.Interpretation;

import java.util.Set;

public class GreetingsState extends State {

    private final String TRANSITION_GREETING_DETECTED = "greetingDetected";

    private State next;


    public GreetingsState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
        next = this;
    }

    @Override
    public Output act() {
        return Output.say("GreetingsState act()");
    }

    @Override
    public Output react(Interpretation input) {
        next = getTransition(TRANSITION_GREETING_DETECTED);
        // or next = this if no greeting was detected

        return Output.say("GreetingsState react()");
    }

    @Override
    public State getNextState() {
        return next;
    }

    @Override
    protected Set<String> getRequiredTransitionNames() {
        return newSet(TRANSITION_GREETING_DETECTED);
    }

    @Override
    protected Set<String> getRequiredParameterNames() {
        return newSet(); // empty set
    }

    @Override
    public boolean isFallbackRequired() {
        return false;
    }
}
