package roboy.newDialog.states;

import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.logic.StatementInterpreter;
import roboy.talk.StatementBuilder;
import roboy.talk.Verbalizer;

import java.util.Set;

public class GreetingsState extends State {

    private final String TRANSITION_GREETING_DETECTED = "greetingDetected";

    private State next;
    private boolean inputOK = true;

    public GreetingsState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
        next = this;
    }

    @Override
    public Output act() {

        return Output.sayNothing();
    }

    @Override
    public Output react(Interpretation input) {

        String sentence = (String) input.getFeatures().get(Linguistics.SENTENCE);
        inputOK = StatementInterpreter.isFromList(sentence, Verbalizer.greetings) ||
                StatementInterpreter.isFromList(sentence, Verbalizer.roboyNames) ||
                StatementInterpreter.isFromList(sentence, Verbalizer.triggers);

        if (inputOK) {
            next = getTransition(TRANSITION_GREETING_DETECTED);
            return Output.say(StatementBuilder.random(Verbalizer.greetings));
        }

        return Output.sayNothing();
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
