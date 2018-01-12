package roboy.newDialog.states;

import com.google.gson.JsonObject;
import roboy.linguistics.sentenceanalysis.Interpretation;

import java.util.*;

/**
 * Central class of the dialog state system. Every dialog state should extend this class.
 * A state always acts when it is entered and reacts when its left. Both, the reaction of
 * the last and the action of the next state, are combined to give the answer of Roboy.
 */
public abstract class State {


    // State name/identifier
    private String stateIdentifier;

    // If this state can't react to the input, the Personality state machine will ask the fallback state
    private State fallback;

    // Possible transitions to other states. The next state is selected based on some conditions in getNextState();
    private HashMap<String, State> transitions;


    public State(String stateIdentifier) {
        this.stateIdentifier = stateIdentifier;
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
     * to react to the input. This state still remains active.
     * @return fallback state
     */
    public final State getFallback() {
        return fallback;
    }

    /**
     * Set the fallback state. The Personality state machine will ask the fallback state if this one has no answer.
     * @param fallback fallback state
     */
    public final void setFallback(State fallback) {
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
    public final void setTransition(String name, State goToState) {
        transitions.put(name, goToState);
    }
    public final State getTransition(String name) {
        return transitions.get(name);
    }
    public final HashMap<String, State> getAllTransitions() {
        return transitions;
    }

    //endregion

    // Functions that must be implemented in sub classes:

    // region to be implemented in subclasses

    /**
     * A state always acts after the reaction. Both, the reaction of the last and the action of the next state,
     * are combined to give the answer of Roboy.
     * @return interpretations
     */
    public abstract List<Interpretation> act();


    /**
     * Defines how to react to an input. This is usually the answer to the incoming question or some other statement.
     * If this state can't react, it can return 'null' to trigger the fallback state for the answer.
     *
     * Note: In the new architecture, react() does not define the next state anymore! Reaction and state
     * transitions are now decoupled. State transitions are defined in getNextState()
     *
     * @param input input from the person we talk to
     * @return reaction to the input OR null (will trigger the fallback state)
     */
    public abstract List<Interpretation> react(Interpretation input);


    /**
     * After this state has reacted, the personality state machine will ask this state to which state to go next.
     * If this state is not ready, it will return itself. Otherwise, depending on internal conditions, this state
     * will select one of the states defined in transitions to be the next one.
     *
     * @return next actie state after this one has reacted
     */
    public abstract State getNextState();


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

    /**
     * Utility function to create and initialize string sets in just one code line.
     * @param tNames names of the required transitions
     * @return set initialized with inputs
     */
    protected Set<String> newSet(String ... tNames) {
        HashSet<String> result = new HashSet<>();
        result.addAll(Arrays.asList(tNames));
        return result;
    }

    //endregion

    public JsonObject toJsonObject() {
        JsonObject stateJson = new JsonObject();
        stateJson.addProperty("identifier", getIdentifier());

        String className = getClass().getCanonicalName();
        stateJson.addProperty("implementation", className);

        if (fallback != null) {
            String fallbackID = fallback.getIdentifier();
            stateJson.addProperty("fallback", fallbackID);
        }

        // transitions
        JsonObject transitionsJson = new JsonObject();
        for (Map.Entry<String, State> transition : getAllTransitions().entrySet()) {
            String transName = transition.getKey();
            String transStateID = transition.getValue().getIdentifier();
            transitionsJson.addProperty(transName, transStateID);
        }
        stateJson.add("transitions", transitionsJson);


        return stateJson;

    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("State ").append(getIdentifier()).append(" of class ");
        s.append(this.getClass().getSimpleName()).append(" {\n");

        State fallback = getFallback();
        if (fallback != null) {
            s.append("  ! fallback: ").append(fallback.getIdentifier()).append("\n");
        }
        for (Map.Entry<String, State> transition : getAllTransitions().entrySet()) {
            s.append("  ").append(transition.getKey()).append(": ");
            s.append(transition.getValue().getIdentifier()).append("\n");
        }

        return s.append("}\n").toString();


    }

    @Override
    public boolean equals(Object obj) {
        if ( ! (obj instanceof State)) {
            return false;
        }
        State other = (State) obj;

        // different class
        if (other.getClass() != this.getClass()) {
            return false;
        }

        // other has a fallback, this doesn't
        if (this.fallback == null && other.fallback != null) {
            return false;
        }

        // this has a fallback, other doesn't
        if (other.fallback == null && this.fallback != null) {
            return false;
        }

        // both have fallbacks, compare them by IDs
        if (this.fallback != null) {
            String thisFallbackID = this.getFallback().getIdentifier();
            String otherFallbackID = this.getFallback().getIdentifier();
            // different fallback IDs
            if (!thisFallbackID.equals(otherFallbackID)) {
                return false;
            }
        }

        // compare transitions: all of this transitions are present in the other
        boolean otherHasAllOfThis = this.equalsHelper_compareTransitions(other);
        boolean thisHasAllOfOther = other.equalsHelper_compareTransitions(this);

        return otherHasAllOfThis && thisHasAllOfOther;
    }

    /**
     * check if every transition of this is present in the other and points to the same ID
     * @param other other state to compare transitions
     * @return true if all transitions of this state are present in the other state
     */
    private boolean equalsHelper_compareTransitions(State other) {

        // for every transition in this state
        for (Map.Entry<String, State> transition : getAllTransitions().entrySet()) {

            // transition name
            String transName = transition.getKey();
            // id of the state this transition points to
            String thisTransStateID = transition.getValue().getIdentifier();

            // check if transition in the other state points to the same id
            State otherTransState = other.getTransition(transName);
            if (otherTransState == null)  return false;

            String otherTransStateID = otherTransState.getIdentifier();
            if (! thisTransStateID.equals(otherTransStateID)) return false;

        }
        return true;

    }


}

