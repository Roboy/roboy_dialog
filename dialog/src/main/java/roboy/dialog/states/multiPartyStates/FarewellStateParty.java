package roboy.dialog.states.multiPartyStates;

import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.talk.Verbalizer;
import roboy.linguistics.Linguistics;
import roboy.logic.StatementInterpreter;
import roboy.util.RandomList;

import java.util.ArrayList;
import java.util.Set;

public class FarewellStateParty extends State {
    private State next = null;
    private int loops = 0;
    private final static int MAX_LOOP_COUNT = 2;
    private static RandomList<String> conversationEndings = new RandomList<>(
            "What a nice conversation! I have to think about everything we" +
                    " were talking about. Let's talk again next time.",
            "I feel tired now, maybe my battery is low? Let's talk again later.",
            "Don't you think that the dialog team is amazing? They are happy to " +
                    "tell you more about my system. Just ask one of them!");

    public FarewellStateParty(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

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
        return null;
    }

    @Override
    public Output react(ArrayList<Interpretation> input){
        int speakerCount = input.get(0).getSpeakerInfo().getSpeakerCount();
        for(int i=0; i<speakerCount; i++){
            String sentence = input.get(i).getSentence();
            if (StatementInterpreter.isFromList(sentence, Verbalizer.farewells)) {
                next = null;
            } else {
                next = this;
                loops++;
            }
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
