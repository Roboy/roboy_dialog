package roboy.newDialog;

import com.google.gson.*;
import roboy.newDialog.states.State;
import roboy.newDialog.states.StateParameters;
import roboy.newDialog.states.factories.ToyStateFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * State machine to manage dialog states.
 * Dialog state machines can be written to files and loaded from them later.
 *
 * Personalities can be implemented using a dialog state machine.
 */
public class DialogStateMachine {

    // maps string identifiers to state objects ("Greeting" -> {GreetingState})
    // allows to have multiple instances of the same state class with different identifiers ("Greeting2" -> {GreetingState})
    private HashMap<String, State> identifierToState;

    private State activeState;
    private State initialState;

    public DialogStateMachine() {
        identifierToState = new HashMap<>();
        activeState = null;
    }

    public State getInitialState() {
        return initialState;
    }
    /**
     * Set the initial state of this state machine.
     * The state will be automatically added to the machine.
     * If active state was null, it will be set to the new initial state.
     * @param initial initial state
     */
    public void setInitialState(State initial) {
        if (initial == null) return;

        if (!identifierToState.containsValue(initial)) {
            addState(initial);
        }
        this.initialState = initial;
        if (activeState == null) {
            setActiveState(initial);
        }
    }
    public void setInitialState(String identifier) {
        State initial = identifierToState.get(identifier);
        if (initial == null) {
            System.err.println("[!!] setInitialState: Unknown identifier: " + identifier);
            return;
        }
        setInitialState(initial);
    }


    public State getActiveState() {
        return activeState;
    }
    public void setActiveState(State s) {
        if (s == null) return;

        if (!identifierToState.containsValue(s)) {
            addState(s);
        }
        activeState = s;
    }
    public void setActiveState(String identifier) {
        State s = identifierToState.get(identifier);
        if (s == null) {
            System.err.println("[!!] setInitialState: Unknown identifier: " + identifier);
            return;
        }
        activeState = s;
    }



    public State getStateByIdentifier(String identifier) {
        return identifierToState.get(identifier);
    }
    public void addState(State s) {
        identifierToState.put(s.getIdentifier(), s);
    }



    public void loadFromString(String s) {
        JsonParser parser = new JsonParser();
        JsonElement json = parser.parse(s);
        loadFromJSON(json);
    }

    public void loadFromFile(File f) throws FileNotFoundException {
        JsonParser parser = new JsonParser();
        JsonElement json = parser.parse(new FileReader(f));
        loadFromJSON(json);
    }


    private void loadFromJSON(JsonElement json) {
        identifierToState.clear();
        activeState = null;
        initialState = null;

        if (!json.isJsonObject()) {
            System.err.println("[!!] loadFromJSON: State machine must be a JSON object!");
            return;
        }
        JsonObject personalityJson = json.getAsJsonObject();

        JsonElement initialStateJson = personalityJson.get("initialState");
        if (initialStateJson == null) {
            System.err.println("[!!] loadFromJSON: Initial state not defined!");
            return;
        }
        String initialStateIdentifier = initialStateJson.getAsString();

        JsonElement statesJson = personalityJson.get("states");
        if (statesJson == null) {
            System.err.println("[!!] loadFromJSON: states not defined!");
            return;
        }
        JsonArray statesJsA = statesJson.getAsJsonArray();

        // for each state: create an object of the correct type and add it to the hash map
        parseAndCreateStates(statesJsA, identifierToState);

        // now all states were converted into objects
        // set initial state
        setInitialState(initialStateIdentifier);  // actually also sets the active state in this case
        setActiveState(initialStateIdentifier);

        // set fallbacks and transitions (if defined)
        parseAndSetTransitionsAndFallbacks(statesJsA, identifierToState);

        // check if all states have all required transitions initialized correctly
        checkSuccessfulInitialization(identifierToState);
    }

    /**
     * Parses parameters from the state json object. A new instance of StateParameters is created.
     * @param stateJsO json object representing a state
     * @return StateParameters instance with all parameters defined in json object
     */
    private StateParameters parseStateParameters(JsonObject stateJsO) {
        StateParameters params = new StateParameters();

        // set the transitions
        JsonObject paramsJsO = stateJsO.getAsJsonObject("parameters");
        if (paramsJsO != null && !paramsJsO.isJsonNull()) {

            for (Map.Entry<String,JsonElement> entry : paramsJsO.entrySet()) {
                String paramName = entry.getKey();
                String paramValue = entry.getValue().getAsString();
                params.set(paramName, paramValue);
            }
        }

        return params;
    }

    /**
     * Parses every element of the json array and creates a state java object.
     * State parameters are parsed before the object is created.
     * @param statesJsA json array containing states
     * @param idToState hash map to store the new state objects
     */
    private void parseAndCreateStates(JsonArray statesJsA, HashMap<String, State> idToState) {
        for (JsonElement stateJsE : statesJsA) {
            JsonObject stateJsO = stateJsE.getAsJsonObject();
            StateParameters params = parseStateParameters(stateJsO);

            String identifier = stateJsO.get("identifier").getAsString();
            String implClassName = stateJsO.get("implementation").getAsString();

            State state = ToyStateFactory.getByClassName(implClassName, identifier, params);
            if (state != null) {
                idToState.put(identifier, state);
            }
        }
    }

    /**
     * Parses every element of the json array (containing states). For every state, finds and sets the fallback if
     * defined. State transitions are also initialized.
     * @param statesJsA json array containing states
     * @param idToState initialized hash map that resolves state IDs to state java objects
     */
    private void parseAndSetTransitionsAndFallbacks(JsonArray statesJsA, HashMap<String, State> idToState) {
        for (JsonElement stateJsE : statesJsA) {
            JsonObject stateJsO = stateJsE.getAsJsonObject();

            String identifier = stateJsO.get("identifier").getAsString();
            State thisState = idToState.get(identifier);

            if (thisState == null) {
                throw new RuntimeException("State with identifier " + identifier + " is missing!");
            }

            // check if fallback is defined
            JsonElement fallbackJsE = stateJsO.get("fallback");
            if (fallbackJsE != null && !fallbackJsE.isJsonNull()) {
                String fallbackIdentifier = fallbackJsE.getAsString();
                if (fallbackIdentifier != null) {
                    State fallbackState = idToState.get(fallbackIdentifier);
                    if (fallbackState == null) {
                        System.err.println("[!!] parseAndSetTransitionsAndFallbacks: fallback "
                                + fallbackIdentifier + " missing!");
                    } else {
                        thisState.setFallback(fallbackState);
                    }
                }
            }


            // set the transitions
            JsonObject transitions = stateJsO.getAsJsonObject("transitions");
            if (transitions != null && !transitions.isJsonNull()) {

                for (Map.Entry<String,JsonElement> entry : transitions.entrySet()) {
                    String tranName = entry.getKey();
                    String transTargetID = entry.getValue().getAsString();
                    State transTargetState = idToState.get(transTargetID);
                    if (transTargetState == null) {
                        System.err.println("[!!] parseAndSetTransitionsAndFallbacks: transition with name "
                                + transTargetID + " has no target!");
                    } else {
                        thisState.setTransition(tranName, transTargetState);
                    }
                }
            }
        }
    }

    /**
     * For every state in the idToState hash map, check if all required transitions and parameters were
     * initialized correctly. Also check if all required fallbacks were set.
     */
    private void checkSuccessfulInitialization(HashMap<String, State> idToState) {

        for (State s : idToState.values()) {
            if (!s.allRequiredTransitionsAreInitialized()) {
                System.err.println("[!!] checkSuccessfulInitialization: Some required transitions are missing in the " +
                        "state with identifier " + s.getIdentifier());
            }
            if (!s.allRequiredParametersAreInitialized()) {
                System.err.println("[!!] checkSuccessfulInitialization: Some required parameters are missing in the " +
                        "state with identifier " + s.getIdentifier());
            }
            if (s.isFallbackRequired() && s.getFallback() == null) {
                System.err.println("[!!] checkSuccessfulInitialization: Fallback is required but missing in the " +
                        "state with identifier " + s.getIdentifier());
            }
        }
    }


    public void saveToFile(File f) throws FileNotFoundException {

        String json = toJsonString();

        try( PrintWriter out = new PrintWriter( f ) ){
            out.println( json );
        }

    }



    private JsonObject toJsonObject() {
        JsonObject stateMachineJson = new JsonObject();
        if (initialState == null) {
            System.err.println("[!!] toJsonObject: initial state undefined!");
        } else {
            stateMachineJson.addProperty("initialState", initialState.getIdentifier());
        }

        // all states
        JsonArray statesJsonArray = new JsonArray();
        for (State state : identifierToState.values()) {
            JsonObject stateJson = state.toJsonObject();
            statesJsonArray.add(stateJson);
        }
        stateMachineJson.add("states", statesJsonArray);

        return stateMachineJson;
    }

    public String toJsonString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject json = toJsonObject();
        return gson.toJson(json);
    }


    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("###################################################\n");
        s.append("Dialog State Machine\n");
        s.append("###################################################\n");

        s.append(">> Current state:\n");
        s.append(activeState).append("\n");

        s.append(">> Initial state:\n");
        s.append(initialState).append("\n");

        s.append(">> All states:\n");
        for (State state : identifierToState.values()) {
            s.append(state);
        }

        s.append("###################################################\n");

        return s.toString();
    }


    @Override
    public boolean equals(Object obj) {
        if ( ! (obj instanceof DialogStateMachine) ) {
            return false;
        }

        // Two state machines are equal if and only if:
        // - they contain the same states (same names + classes)
        // - the initial state is identical
        // - they states are identically connected (transitions + fallbacks)

        // The active state is not important for the structure and will be ignored by this check!
        // State parameters are also ignored

        DialogStateMachine other = (DialogStateMachine) obj;

        // check initial states
        if (initialState == null && other.initialState != null) {
            return false;
        }
        if (initialState != null && (!initialState.equals(other.initialState)) ) {
            return false;
        }


        // all states + transitions from this machine are present in the other
        for (State thisState : identifierToState.values()) {
            String stateID = thisState.getIdentifier();
            State otherState = other.getStateByIdentifier(stateID);
            if (otherState == null)   return false;
            if ( ! thisState.equals(otherState))  return false;
        }


        // all states + transitions from the other machine are present in this
        for (State otherState : other.identifierToState.values()) {
            String stateID = otherState.getIdentifier();
            State thisState = this.getStateByIdentifier(stateID);
            if (thisState == null) return false;
            if ( ! thisState.equals(otherState)) return false;
        }

        return true;
    }


}
