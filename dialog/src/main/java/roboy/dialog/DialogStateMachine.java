package roboy.dialog;

import com.google.gson.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.context.Context;
import roboy.logic.InferenceEngine;
import roboy.memory.Neo4jMemoryInterface;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateFactory;
import roboy.dialog.states.definitions.StateParameters;
import roboy.ros.RosMainNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * State machine to manage dialog states. State based personality is built on top of this class.
 *
 * Main functionality of this class enables loading dialog state machines from personality files.
 * There is also an option to save an existing state machine to file.
 *
 * Personality files are JSON files that define a set of dialog states and transitions between them
 * (see examples in resources/personalityFiles/tutorial/). Every state definition in the file has an identifier
 * and specifies the implementation (class name) for the state. During parsing of the personality file
 * this class will take the class name and create a Java State object using Java reflection.
 */
public class DialogStateMachine {


    // #####################################################
    // #             Variables & Constructors              #
    // #####################################################

    //region variables

    private final Logger logger = LogManager.getLogger();

    /**
     * maps string identifiers to state objects ("Greeting" -> {GreetingState})
     * allows to have multiple instances of the same state class with different identifiers ("Greeting2" -> {GreetingState})
     */
    private HashMap<String, State> identifierToState;

    private State activeState;
    private State initialState;

    /** RosMainNode will be passed to every state as parameter */
    private final RosMainNode rosMainNode;
    private final Neo4jMemoryInterface memory;
    private final InferenceEngine inference;
    private final Context context;

    /**
     * Personality file additional information: everything like comment goes here.
     * [!!] Do not use it in your State implementation! This info is only stored to make sure
     *      we don't lose the comment etc. when saving this dialog state machine to file.
     */
    private HashMap<String, String> optionalPersFileInfo;

    // endregion

    //region constructors

    /**
     * Create an empty DialogStateMachine. Use loadFromFile(...) to load definitions from the
     * personality file after creation. Alternatively you can create and add States to the machine
     * manually from code.
     *
     * @param context reference to the context of the conversation this Statemachine belongs to (will be passed accessible to every newly created State object)
     * @param rosMainNode reference to the RosMainNode that will be passed to every newly created State object
     * @param memory reference to Memory that will be passed to every newly created State object
     */
    public DialogStateMachine(InferenceEngine inference, Context context, RosMainNode rosMainNode, Neo4jMemoryInterface memory) {
        identifierToState = new HashMap<>();
        activeState = null;
        this.rosMainNode = rosMainNode;
        this.memory = memory;
        this.inference = inference;
        optionalPersFileInfo = new HashMap<>();
        this.context = context;

        if (this.rosMainNode == null) {
            logger.info("RosMainNode will be unavailable in DialogStateMachine (null was passed)");
        }
        if (this.memory == null) {
            logger.info("Memory will be unavailable in DialogStateMachine (null was passed)");
        }
        if (this.inference == null) {
            logger.error("All your inference belong to us (null was passed)!");
            throw new IllegalArgumentException("The inference is null. Roboy is a vegetable! Pulling the plug!");
        }
        if (this.context == null){
            logger.error("A conversation without context is not interpretable! (null was passed)");
            throw new IllegalArgumentException("The context is null. Roboy cannot talk without context. Stopping all dialog activity!");
        }
    }

    /**
     * Create an empty OFFLINE DialogStateMachine without a reference to the RosMainNode and Memory.
     * States will not be able to access the RosMainNode and Memory functionality.
     * This constructor is mainly used for testing.
     */
    public DialogStateMachine(InferenceEngine inference, Context context) {
        this(inference, context, null, null);
    }

    //endregion


    // #####################################################
    // #                 Getter & Setter                   #
    // #####################################################

    //region initial state

    /**
     * Getter for the InferenceEngine to be accessible externally.
     * Let semblance of intelligence penetrate the mind of Roboy
     * @return the reference to the inference
     */
    public InferenceEngine getInference() {
        return inference;
    }

    public Context getContext() { return context; }

    /**
     * Returns the initial state for this state machine.
     * @return initial state for this state machine
     */
    public State getInitialState() {
        return initialState;
    }

    /**
     * Set the initial state of this state machine.
     * The state will be automatically added to the machine if not already added.
     * If active state was null, it will be set to the new initial state.
     * @param initial initial state
     */
    public void setInitialState(State initial) {
        if (initial != null && !identifierToState.containsValue(initial)) {
            addState(initial);
        }
        this.initialState = initial;
        if (activeState == null) { // if active state is not set yet, aet active to initial
            setActiveState(initial);
        }
    }

    /**
     * Set the initial state of this state machine using state identifier.
     * If there is no state with specified identifier, you will get an error message
     * and the initial state will be set to null.
     * @param identifier identifier of the state that should become the initial state
     */
    public void setInitialState(String identifier) {
        State initial = identifierToState.get(identifier);
        if (initial == null) {
            logger.error("setInitialState(" + identifier + "): Unknown identifier!");
        }
        setInitialState(initial);
    }

    //endregion

    //region active state

    /**
     * Returns the active state for this state machine.
     * @return active state for this state machine
     */
    public State getActiveState() {
        return activeState;
    }

    /**
     *
     * Set the active state of this state machine.
     * The state will be automatically added to the machine if not already added.
     * @param s state to make active
     */
    public void setActiveState(State s) {
        if (s != null && !identifierToState.containsValue(s)) {
            addState(s);
        }
        activeState = s;
    }

    /**
     * Set the active state using state identifier.
     * If there is no state with specified identifier, you will get an error message and
     * active state will be set to null.
     * @param identifier identifier of the state that should become the active state
     */
    public void setActiveState(String identifier) {
        State s = identifierToState.get(identifier);
        if (s == null) {
            logger.error("setActiveState(" + identifier + "): Unknown identifier!");
        }
        activeState = s;
    }

    //endregion

    //region get by id & add state

    /**
     * Returns a state with given identifier. Returns null if no such state was previously added to the machine.
     * @param identifier identifier of the state to retrieve
     * @return state with given identifier or null
     */
    public State getStateByIdentifier(String identifier) {
        return identifierToState.get(identifier);
    }

    /**
     * Add a state to this state machine.
     * @param s state to add.
     */
    public void addState(State s) {
        if (s == null) {
            logger.warn("trying to add null to (ID->State) hash map!");
            return;
        }
        identifierToState.put(s.getIdentifier(), s);
    }

    //endregion


    // #####################################################
    // #                Loading & Parsing                  #
    // #####################################################

    //region load from ...

    /**
     * Loads state machine from a JSON string. The string must be a valid personality
     * (usually loaded from a personality file).
     * @param s personality string
     */
    public void loadFromString(String s) {
        JsonParser parser = new JsonParser();
        JsonElement json = parser.parse(s);
        loadFromJSON(json);
    }

    /**
     * Loads state machine from a personality file. File must contain a valid personality definition.
     * @param f file with the personality definition
     * @throws FileNotFoundException if file is not found
     */
    public void loadFromFile(File f) throws FileNotFoundException {
        JsonParser parser = new JsonParser();
        JsonElement json = parser.parse(new FileReader(f));
        loadFromJSON(json);
    }


    /**
     * Main function that parses a JSON personality object and creates a state machine.
     * Required properties:
     * - initialState (string identifier)
     * - states (array of state definitions)
     * Optional properties:
     * - comment (personality file comment)
     * @param json json object with the personality definition
     */
    private void loadFromJSON(JsonElement json) {
        identifierToState.clear();
        optionalPersFileInfo.clear();
        activeState = null;
        initialState = null;

        if (!json.isJsonObject()) {
            logger.error("loadFromJSON(): State machine must be a JSON object!");
            return;
        }
        JsonObject personalityJson = json.getAsJsonObject();

        JsonElement commentJson = personalityJson.get("comment");
        if (commentJson != null) {
            optionalPersFileInfo.put("comment", commentJson.getAsString());
        }

        JsonElement initialStateJson = personalityJson.get("initialState");
        if (initialStateJson == null) {
            logger.error("loadFromJSON(): Initial state not defined!");
            return;
        }
        String initialStateIdentifier = initialStateJson.getAsString();

        JsonElement statesJson = personalityJson.get("states");
        if (statesJson == null) {
            logger.error("loadFromJSON(): states not defined!");
            return;
        }
        JsonArray statesJsA = statesJson.getAsJsonArray();

        // for each state: create an object of the correct type and add it to the hash map
        parseAndCreateStates(statesJsA);

        // now all states were converted into objects
        // set initial state
        setInitialState(initialStateIdentifier);  // actually also sets the active state in this case
        setActiveState(initialStateIdentifier);

        // set fallbacks and transitions (if defined)
        parseAndSetTransitionsAndFallbacks(statesJsA, identifierToState);

        // check if all states have all required transitions initialized correctly
        checkSuccessfulInitialization(identifierToState);
    }

    //endregion

    //region parsing & init checks

    /**
     * Parses parameters from the state json object. A new instance of StateParameters is created.
     * @param stateJsO json object representing a state
     * @return StateParameters instance with all parameters defined in json object
     */
    private StateParameters parseStateParameters(JsonObject stateJsO) {
        StateParameters params = new StateParameters(this, rosMainNode, memory);

        // set the transitions
        JsonObject paramsJsO = stateJsO.getAsJsonObject("parameters");
        if (paramsJsO != null && !paramsJsO.isJsonNull()) {

            for (Map.Entry<String,JsonElement> entry : paramsJsO.entrySet()) {
                String paramName = entry.getKey();
                String paramValue = entry.getValue().getAsString();
                params.setParameter(paramName, paramValue);
            }
        }

        return params;
    }

    /**
     * Parses every element of the json array and creates a state java object.
     * State parameters are parsed before the object is created.
     * @param statesJsA json array containing states
     */
    private void parseAndCreateStates(JsonArray statesJsA) {
        for (JsonElement stateJsE : statesJsA) {
            JsonObject stateJsO = stateJsE.getAsJsonObject();
            StateParameters params = parseStateParameters(stateJsO);

            String identifier = stateJsO.get("identifier").getAsString();
            String implClassName = stateJsO.get("implementation").getAsString();

            // create state object by class name with java reflection
            State state = StateFactory.createStateByClassName(implClassName, identifier, params);
            if (state == null) {
                logger.error("parseAndCreateStates(): state " + identifier + " was not created!");
                continue;
            }

            // set optional values
            JsonElement commentJson = stateJsO.get("comment");
            if (commentJson != null) {
                state.setOptionalPersFileInfo("comment", commentJson.getAsString());
            }

            addState(state);

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
                        logger.error("parseAndSetTransitionsAndFallbacks(): fallback "
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
                        logger.error("parseAndSetTransitionsAndFallbacks(): transition with name "
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
                logger.error("checkSuccessfulInitialization(): Some required transitions are missing in the " +
                        "state with identifier " + s.getIdentifier());
            }
            if (!s.allRequiredParametersAreInitialized()) {
                logger.error("checkSuccessfulInitialization(): Some required parameters are missing in the " +
                        "state with identifier " + s.getIdentifier());
            }
            if (s.isFallbackRequired() && s.getFallback() == null) {
                logger.error("checkSuccessfulInitialization(): Fallback is required but missing in the " +
                        "state with identifier " + s.getIdentifier());
            }

        }
    }

    //endregion


    // #####################################################
    // #           Saving, toString, equals                #
    // #####################################################

    //region save to file / JSON object

    /**
     * Save this state machine to a personality file in JSON format.
     * @param f file to save
     */
    public void saveToFile(File f) throws FileNotFoundException {

        String json = toJsonString();

        try( PrintWriter out = new PrintWriter( f ) ){
            out.println( json );
        }
        // Not catching the exception here! It should be handled inside the code that calls this function.
    }

    /**
     * Creates a JSON object that represents this state machine.
     * @return JSON object that represents this state machine
     */
    private JsonObject toJsonObject() {
        JsonObject stateMachineJson = new JsonObject();

        if (optionalPersFileInfo.containsKey("comment")) {
            stateMachineJson.addProperty("comment", optionalPersFileInfo.get("comment"));
        }

        if (initialState == null) {
            logger.error("toJsonObject(): initial state undefined!");
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

    //endregion

    //region toJsonString & toString

    /**
     * Creates a JSON string that represents this state machine.
     * The JSON string is different from the toString representation which is more readable.
     * @return JSON string that represents this state machine
     */
    public String toJsonString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
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

    //endregion

    //region equals

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

    //endregion

}
