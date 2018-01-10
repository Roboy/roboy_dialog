package roboy.dialog.personality.experimental;

import roboy.dialog.action.Action;
import roboy.dialog.personality.Personality;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.talk.Verbalizer;

import java.util.ArrayList;
import java.util.List;

public class StateBasedPersonality extends DialogStateMachine implements Personality {


    private final Verbalizer verbalizer;


    public StateBasedPersonality(Verbalizer verb) {
        verbalizer = verb;
        reset();
    }


    private void reset() {
        // go back to initial state
        setActiveState(getInitialState());
    }

    public List<Action> startConversation() {

        List<Action> startActions = new ArrayList<>();

        AbstractState activeState = getActiveState();
        if (activeState == null) {
            System.out.println("[!!] This personality state machine has not been initialized!");
            return startActions;
        }


        List<Interpretation> nextAction = activeState.act();
        if (nextAction != null) {
            // next state wants to act -> verbalize it
            for (Interpretation i : nextAction) {
                startActions.add(verbalizer.verbalize(i));
            }
        }
        return startActions;

    }


    @Override
    public List<Action> answer(Interpretation input) {

        List<Action> answerActions = new ArrayList<>();

        AbstractState activeState = getActiveState();
        if (activeState == null) {
            System.out.println("[!!] This personality state machine has not been initialized!");
            return answerActions;
        }
        AbstractState fallback = activeState.getFallback();


        // SPECIAL NON-STATE BASED BEHAVIOUR

        // TODO: special treatment for profanity, farewell phrases etc.


        // REACT TO INPUT

        List<Interpretation> reaction = activeState.react(input);

        // fallbacks
        int fallbackCount = 0, maxFallbackCount = 1000;  // limit to prevent infinite loops
        while (reaction == null && fallback != null && fallbackCount < maxFallbackCount) {
            // active state doesn't know how to react
            // ask fallbacks recursively until
            // - there is a reaction
            // - there is no fallback
            reaction = fallback.react(input);
            fallback = fallback.getFallback();

            // prevent infinite loops
            fallbackCount++;
        }
        if (fallbackCount >= maxFallbackCount)  System.out.println("[!!] Warning: possibly infinite fallback loop");


        if (reaction != null) {
            // there is a reaction -> verbalize it
            for (Interpretation i : reaction) {
                answerActions.add(verbalizer.verbalize(i));
            }
        }


        // MOVE TO THE NEXT STATE

        AbstractState next = activeState.getNextState();
        if (next == null) {
            reset(); // go back to initial state
            return answerActions;
        }
        setActiveState(next);


        // ACT NEXT STATE

        List<Interpretation> nextAction = next.act();
        if (nextAction != null) {
            // next state wants to act -> verbalize it
            for (Interpretation i : nextAction) {
                answerActions.add(verbalizer.verbalize(i));
            }
        }

        return answerActions;
    }
}
