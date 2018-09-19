package roboy.dialog.states.botboy;

import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.logic.StatementInterpreter;
import roboy.talk.Verbalizer;
import roboy.util.RandomList;

import java.util.Set;

/**
 * This state ends the conversation.
 *
 * BotBoyFarewellState interface:
 * 1) Fallback is not required.
 * 2) This state has no outgoing transitions.
 * 3) No parameters are used.
 */

public class BotBoyFarewellState extends State {
    private State next = null;
    private int loops = 0;
    private final static int MAX_LOOP_COUNT = 2;

    public BotBoyFarewellState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

    @Override
    public Output act() {
        if (loops > MAX_LOOP_COUNT) {
            // force conversation stop after a few loops
            return Output.endConversation(Verbalizer.farewells.getRandomElement());
        }
        return Output.say(Verbalizer.farewells.getRandomElement());
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
