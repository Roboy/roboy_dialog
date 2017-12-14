package roboy.dialog.personality.experimental.toyStates;

import roboy.dialog.personality.experimental.AbstractState;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.logic.StatementInterpreter;
import roboy.talk.Verbalizer;
import roboy.util.Lists;

import java.util.*;

/**
 * Toy greeting state: two outgoing transitions "next" and "noHello" have to be defined.
 */
public class ToyGreetingsState extends AbstractState {

    private boolean inputOK = true;

    public ToyGreetingsState(String stateIdentifier) {
        super(stateIdentifier);
    }

    @Override
    public List<Interpretation> act() {
        return Lists.interpretationList(new Interpretation(Linguistics.SENTENCE_TYPE.GREETING));
    }

    @Override
    public List<Interpretation> react(Interpretation input) {
        String sentence = (String) input.getFeatures().get(Linguistics.SENTENCE);
        inputOK = StatementInterpreter.isFromList(sentence, Verbalizer.greetings);

        if (inputOK) {
            return Lists.interpretationList(new Interpretation("I like it when you greet me!"));

        } else {
            return null; // -> fallback state will be used
            // alternatively: return Lists.interpretationList(new Interpretation("Got no greeting :("));
        }
    }


    @Override
    public AbstractState getNextState() {
        if (inputOK) {
            return getTransition("next");

        } else {
            return getTransition("noHello");
            // alternatively: return this to repeat
        }
    }



    @Override
    protected Set<String> getRequiredTransitionNames() {
        // optional: define all required transitions here:
        return newSet("next", "noHello");
    }
}
