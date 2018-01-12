package roboy.newDialog;

import com.google.gson.*;
import roboy.newDialog.states.State;
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
 *
 */
public class DialogStateMachine {

    // maps string identifiers to state objects ("Greeting" -> {GreetingState})
    // allows to have multiple instances of the same state class with different identifiers ("Greeting2" -> {GreetingState})
    private HashMap<String, State> identifierToState;

    private State activeState;
    private State initalState;

    public DialogStateMachine() {
        identifierToState = new HashMap<>();
        activeState = null;

    }

    public State getInitialState() {
        return initalState;
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
        this.initalState = initial;
        if (activeState == null) {
            setActiveState(initial);
        }
    }
    public void setInitialState(String identifier) {
        State initial = identifierToState.get(identifier);
        if (initial == null) {
            System.out.println("Unknown identifier: " + identifier);
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
            System.out.println("Unknown identifier: " + identifier);
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
        initalState = null;

        if (!json.isJsonObject()) {
            System.out.printf("State machine must be a JSON object!");
            return;
        }

        JsonObject personalityJson = json.getAsJsonObject();
        //System.out.println("jsonObject: " + personalityJson);

        JsonElement initialStateJson = personalityJson.get("initialState");
        if (initialStateJson == null) {
            System.out.printf("initial state not defined!");
            return;
        }
        String initialStateIdentifier = initialStateJson.getAsString();


        JsonElement statesJson = personalityJson.get("states");
        if (statesJson == null) {
            System.out.printf("states not defined!");
            return;
        }
        JsonArray states = statesJson.getAsJsonArray();

        // for each state: create an object of the correct type
        // and add it to the hash map
        for (JsonElement state : states) {
            JsonObject s = state.getAsJsonObject();

            String identifier = s.get("identifier").getAsString();
            String implementation = s.get("implementation").getAsString();

            State object = ToyStateFactory.getByClassName(implementation, identifier);
            if (object != null) {
                identifierToState.put(identifier, object);
            }
        }


        // now all states were converted into objects
        // set initial state
        setInitialState(initialStateIdentifier);  // actually also sets the active state in this case
        setActiveState(initialStateIdentifier);


        // set fallbacks and transitions (if defined)
        for (JsonElement state : states) {
            JsonObject s = state.getAsJsonObject();

            String identifier = s.get("identifier").getAsString();
            State thisState = identifierToState.get(identifier);

            if (thisState == null) {
                throw new RuntimeException("State with identifier " + identifier + " is missing!");
            }


            // check if fallback is defined
            JsonElement fallbackEntry = s.get("fallback");
            if (fallbackEntry != null && !fallbackEntry.isJsonNull()) {
                String fallbackIdentifier = fallbackEntry.getAsString();
                if (fallbackIdentifier != null) {
                    State fallbackState = identifierToState.get(fallbackIdentifier);
                    if (fallbackState == null) {
                        System.out.println("fallback " + fallbackIdentifier + " missing");
                    } else {
                        thisState.setFallback(fallbackState);
                    }
                }
            }


            // set the transitions
            JsonObject transitions = s.getAsJsonObject("transitions");
            if (transitions != null && !transitions.isJsonNull()) {

                for (Map.Entry<String,JsonElement> entry : transitions.entrySet()) {
                    String transitionName = entry.getKey();
                    String transitionTarget = entry.getValue().getAsString();

                    State transitionState = identifierToState.get(transitionTarget);

                    if (transitionState != null) {
                        thisState.setTransition(transitionName, transitionState);
                    }
                }
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
        if (initalState == null) {
            System.out.println("initial state undefined!");
        } else {
            stateMachineJson.addProperty("initialState", initalState.getIdentifier());
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
        s.append(initalState).append("\n");

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

        DialogStateMachine other = (DialogStateMachine) obj;

        // check initial states
        if (initalState == null && other.initalState != null) {
            return false;
        }
        if (initalState != null && (!initalState.equals(other.initalState)) ) {
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
