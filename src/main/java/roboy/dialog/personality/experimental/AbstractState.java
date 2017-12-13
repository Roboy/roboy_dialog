package roboy.dialog.personality.experimental;

import roboy.dialog.personality.states.Reaction;
import roboy.linguistics.sentenceanalysis.Interpretation;

import java.util.*;

public abstract class AbstractState {


    // State name/identifier
    private String stateIdentifier;

    // If this state can't react to the input, the Personality state machine will ask the fallback state
    private AbstractState fallback;

    // Possible transitions to other states. The next state is selected based on some conditions in getNextState();
    private HashMap<String, AbstractState> transitions;


    public AbstractState() {
        stateIdentifier = "UndefinedStateIdentifier " + Math.random();
        fallback = null;
        transitions = new HashMap<>();
    }

    //region identifier, fallback & transitions

    public String getIdentifier() {
        return stateIdentifier;
    }
    public void setIdentifier(String stateIdentifier) {
        this.stateIdentifier = stateIdentifier;
    }

    /**
     * If this state can't react to the input, the Personality state machine will ask the fallback state
     * to reat to the input. This state still remains active.
     * @return fallback state
     */
    public final AbstractState getFallback() {
        return fallback;
    }

    /**
     * Set the fallback state. The Personality state machine will ask the fallback state if this one has no answer.
     * @param fallback fallback state
     */
    public final void setFallback(AbstractState fallback) {
        this.fallback = fallback;
    }

    /**
     * Define a possible transition from this state to another. Something like:
     *   "next"      -> {GreetingState}
     *   "rudeInput" -> {EvilState}
     * The next active state will be selected in getNextState() based on internal conditions.
     *
     * @param name  name of the transition
     * @param goToState  state to transit to
     */
    public final void setTransition(String name, AbstractState goToState) {
        transitions.put(name, goToState);
    }
    public final AbstractState getTransition(String name) {
        return transitions.get(name);
    }
    public final HashMap<String, AbstractState> getAllTransitions() {
        return transitions;
    }

    //endregion

    // Functions that must be implemented in sub classes:
    // region to be implemented in subclasses

    /**
     * After this state has reacted, the personality state machine will ask this state to which state to go next.
     * If this state is not ready, it will return itself. Otherwise, depending on internal conditions, this state
     * will select one of the states defined in transitions to be the next one.
     *
     * @return next actie state after this one has reacted
     */
    public abstract AbstractState getNextState();

    /**
     * A state always acts after the reaction. Both, the reaction of the last and the action of the next state,
     * are combined to give the answer of Roboy.
     * @return interpretations
     */
    public abstract List<Interpretation> act();

    /**
     * Defines how to react to an input. This is usually the answer to the incoming question or some other statement.
     * @param input input from the person we talk to
     * @return reaction to the input
     */
    public abstract Reaction react(Interpretation input);

    //endregion

    // Utility functions: make sure initialization is correct
    //region correct initialization checks

    /**
     * Defines the names of all transition that HAVE to be defined for this state.
     * This function is used by allRequiredTransitionsAreInitialized() to make sure this state was
     * initialized correctly. Default implementation requires no transitions to be defined.
     *
     * Override this function in sub classes.
     * @return a set of transition names that have to be defined
     */
    protected Set<String> getRequiredTransitionNames() {
        // default implementation: no required transitions
        return new HashSet<>();
    }

    /**
     * Checks if all required transitions were initialized correctly.
     * Required transitions are defined in getRequiredTransitionNames().
     *
     * @return true if this state was initialized correctly
     */
    public final boolean allRequiredTransitionsAreInitialized() {
        boolean allGood = true;

        for (String tName : getRequiredTransitionNames()) {
            if (!transitions.containsKey(tName)) {
                System.err.println("Transition " + tName + " is required but is not defined!");
                allGood = false;
            }
        }

        return allGood;
    }

    //endregion

}
