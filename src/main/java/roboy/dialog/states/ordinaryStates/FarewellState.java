package roboy.dialog.states.ordinaryStates;

import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.logic.StatementInterpreter;
import roboy.talk.Verbalizer;
import roboy.util.RandomList;

import java.util.Set;

/**
 * This state ends the conversation.
 *
 * FarewellState interface:
 * 1) Fallback is not required.
 * 2) This state has no outgoing transitions.
 * 3) No parameters are used.
 */
public class FarewellState extends State {

    private State next = null;
    private int loops = 0;
    private final static int MAX_LOOP_COUNT = 2;

    public FarewellState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

    private static RandomList<String> conversationEndings = new RandomList<>(
            "What a nice conversation! I have to think about everything we" +
                    " were talking about. Let's talk again next time.",
            "I feel tired now, maybe my battery is low? Let's talk again later.",
            "Don't you think that the dialog team is amazing? They are happy to " +
                    "tell you more about my system. Just ask one of them!");

    @Override
    public Output act() {
        if (loops > MAX_LOOP_COUNT) {
            // force conversation stop after a few loops
            return Output.endConversation(Verbalizer.farewells.getRandomElement());
        }
        return Output.say(conversationEndings.getRandomElement());
    }

    @Override
    public Output react(Interpretation input) {

        String sentence = input.getSentence();
        if (StatementInterpreter.isFromList(sentence, Verbalizer.farewells)) {
            next = null;
        } else {
            next = this;
            loops++;
        }

        return Output.sayNothing();
    }

    @Override
    public State getNextState() {
        return next;
    }

    @Override
    protected Set<String> getRequiredTransitionNames() {
        return newSet();
    }

    @Override
    protected Set<String> getRequiredParameterNames() {
        return newSet();
    }

    @Override
    public boolean isFallbackRequired() {
        return false;
    }
}
