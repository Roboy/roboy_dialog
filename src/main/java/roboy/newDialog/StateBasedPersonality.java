package roboy.newDialog;

import roboy.dialog.action.Action;
import roboy.dialog.action.FaceAction;
import roboy.dialog.personality.Personality;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.newDialog.states.State;
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

    /**
     * Always called once by the (new) DialogSystem at the beginning of every new conversation.
     * @return list of actions based on act() of the initial/active state
     */
    public List<Action> startConversation() {
        State activeState = getActiveState();
        if (activeState == null) {
            System.out.println("[!!] ERROR: This personality state machine has not been initialized!");
            return new ArrayList<>();
        }
        if ( ! activeState.equals(getInitialState())) {
            System.out.println("[!!] WARNING: The active state is different from the initial state " +
                    "at the beginning of conversation!");
        }


        return stateAct(activeState);
    }


    @Override
    public List<Action> answer(Interpretation input) {

        State activeState = getActiveState();
        if (activeState == null) {
            System.out.println("[!!] ERROR: This personality state machine has not been initialized!");
            return new ArrayList<>();
        }
        List<Action> answerActions = new ArrayList<>();


        // SPECIAL NON-STATE BASED BEHAVIOUR
        // TODO: special treatment for profanity, farewell phrases etc.
        if (input.getFeatures().containsKey(Linguistics.EMOTION)) {
            // change facial expression based on input
            answerActions.add(new FaceAction((String) input.getFeatures().get(Linguistics.EMOTION)));
        }


        // ACTIVE STATE REACTS TO INPUT
        List<Action> react = stateReact(activeState, input);
        answerActions.addAll(react);


        // MOVE TO THE NEXT STATE
        State next = activeState.getNextState();
        if (next == null) {
            reset(); // go back to initial state
            return answerActions;
        }
        setActiveState(next);


        // NEXT STATE ACTS
        List<Action> act = stateAct(next);
        answerActions.addAll(act);

        return answerActions;
    }


    /**
     * Call the act function of the state and verbalize all interpretations into actions.
     * @param state state to call ACT on
     * @return list of actions
     */
    private List<Action> stateAct(State state) {
        List<Interpretation> stateActIntepretations = state.act();
        return verbalizeInterpretations(stateActIntepretations);
    }


    /**
     * Call the react function of the state. If the state can't react, recursively ask fallbacks.
     * Verbalize the resulting reaction interpretation into actions.
     *
     * @param state state to call REact on
     * @param input input from the person Roboy speaks to
     * @return list of actions
     */
    private List<Action> stateReact(State state, Interpretation input) {

        List<Interpretation> reaction = state.react(input);
        State fallback = state.getFallback();

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
        if (fallbackCount >= maxFallbackCount)  System.out.println("[!!] WARNING: possibly infinite fallback loop");

        return verbalizeInterpretations(reaction);
    }


    /**
     * Verbalizes all interpretations into actions using the verbalizer.
     * @param interpretations list of interpretations.
     * @return list of actions
     */
    private List<Action> verbalizeInterpretations(List<Interpretation> interpretations) {
        List<Action> listOfActions = new ArrayList<>();
        if (interpretations != null) {
            // verbalize all act interpretations
            for (Interpretation i : interpretations) {
                listOfActions.add(verbalizer.verbalize(i));
            }
        }
        return listOfActions;
    }


}
