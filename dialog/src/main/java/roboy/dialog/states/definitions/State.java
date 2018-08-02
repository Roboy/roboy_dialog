package roboy.dialog.states.definitions;

import com.google.gson.Gson;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.context.Context;
import roboy.emotions.RoboyEmotion;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.logic.InferenceEngine;
import roboy.memory.Neo4jMemoryInterface;
import roboy.memory.nodes.MemoryNodeModel;
import roboy.dialog.DialogStateMachine;
import roboy.dialog.Segue;
import roboy.ros.RosMainNode;
import roboy.util.RandomList;

import java.io.IOException;
import java.util.*;

/**
 * Central class of the dialog state system. Every dialog state should extend this class.
 * A state always acts when it is entered and reacts when its left. Both, the reaction of
 * the last and the action of the next state, are combined to give the answer of Roboy.
 *
 * A state can have any number of transitions to other states. Every transition has a name
 * (like "next" or "errorState"). When designing a new state, only the transition names
 * are known. At run time the transitions will point to other states. You can get the
 * attached state by the transition name using getTransition(transitionName).
 *
 * A fallback can be attached to a state. In the case this state doesn't know how to react
 * to an utterance, it can return Output.useFallback() from the react() function. The state
 * machine will query the fallback in this case. More details on the fallback concept can
 * be found in the description of the StateBasedPersonality and in comments below.
 */
public abstract class State {


    // #####################################################
    // #             Output static inner class             #
    // #####################################################

    // region Output static inner class

    /**
     *  Output static inner class represents the return values of act() and react() methods.
     *  There are four possible scenarios:
     *   - the state wants to say something         -> a single interpretation is returned
     *   - the state does not say anything          -> no interpretation
     *   - the state does not know how to react     -> fallback state is required to fix this
     *   - the state wants to end the conversation  -> reset the whole dialog state machine
     *
     *   To create an instance of this class inside the act() or react() method use following:
     *   - Output.say( new Interpretation(...) )  - to return an interpretation
     *   - Output.say( "Some phrase here" )       - to return an interpretation (will be created from string)
     *   - Output.sayNothing()                    - to make clear that you don't want to say anything
     *   - Output.useFallback()                   - to indicate that you can't react and want to use the fallback
     *   - Output.endConversation()               - to stop the conversation immediately and reset the state machine
     *   - Output.endConversation( "last words" ) - to say the last words and reset the state machine afterwards
     */
    public static class Output {

        private final Logger logger = LogManager.getLogger();

        public enum OutputType {
            INTERPRETATION, SAY_NOTHING, USE_FALLBACK, END_CONVERSATION
        }

        private final OutputType type;
        private final Interpretation interpretation;
        private Segue segue;
        private RoboyEmotion emotion;

        /**
         * Private constructor, used only inside static methods.
         * @param type type of this react object
         * @param interpretation optional interpretation object (or null)
         */
        private Output(OutputType type, Interpretation interpretation) {
            this.type = type;
            this.interpretation = interpretation;
            this.segue = null;
            this.emotion = interpretation.getEmotion();
        }

        //  Static creators

        /**
         * Say a phrase.
         * @param i interpretation object that contains the phrase
         * @return State.Output object with appropriate settings
         */
        public static Output say(Interpretation i) {
            if (i == null) {
                return sayNothing();
            }
            return new Output(OutputType.INTERPRETATION, i);
        }

        /**
         * Say a phrase.
         * @param s phrase to say
         * @return State.Output object with appropriate settings
         */
        public static Output say(String s) {
            if (s == null || s.equals("")) {
                return sayNothing();
            }
            return say(new Interpretation(s));
        }

        /**
         * Say nothing (as the output of State act/react).
         * @return State.Output object with appropriate settings
         */
        public static Output sayNothing() {
            return new Output(OutputType.SAY_NOTHING, null);
        }

        /**
         * Indicate that current state has no idea how to react and that the dialog system
         * should use a fallback state to react instead. This option is only allowed for react(...) output.
         * States should never use this option from the act() function.
         * @return State.Output object with appropriate settings
         */
        public static Output useFallback() {
            return new Output(OutputType.USE_FALLBACK, null);
        }

        /**
         * End the conversation immediately.
         * @return State.Output object with appropriate settings
         */
        public static Output endConversation() {
            return new Output(OutputType.END_CONVERSATION, null);
        }

        /**
         * End the conversation after saying some last words
         * @param lastWords last word to say (something like "I'll be back")
         * @return State.Output object with appropriate settings
         */
        public static Output endConversation(String lastWords) {
            if (lastWords == null) {
                return endConversation();
            }
            return new Output(OutputType.END_CONVERSATION, new Interpretation(lastWords));
        }


        // Non-static methods

        public boolean hasInterpretation() {
            return type == OutputType.INTERPRETATION; // interpretation != null
        }

        public boolean requiresFallback() {
            return type == OutputType.USE_FALLBACK;
        }

        public boolean isEmpty() {
            return type == OutputType.SAY_NOTHING;
        }

        public boolean isEndOfConversation() {
            return type == OutputType.END_CONVERSATION;
        }

        public Interpretation getInterpretation() {
            return interpretation;
        }


        // Additional transition actions: Segues

        /**
         * A segue is a smooth transition from one topic to the next. You can add a segues of a specific
         * types to the state output if you want to change the topic. Segues have a certain probabilities
         * to be used and are always added after the original output was said.
         * @param s segue to add
         * @return the same Output object so you can chain multiple function calls on it
         */
        public Output setSegue(Segue s) {
            if (type == OutputType.USE_FALLBACK) {
                logger.warn("Adding a segue to an answer that requires fallback is not allowed! " +
                        "Segue behaviour is defined in the fallback state.");
            }
            segue = s;
            return this;
        }
        public boolean hasSegue() {
            return segue != null;
        }
        public Segue getSegue() {
            return segue;
        }

        // Emotion
        /*
         * @param s emotion to add
         * @return the same Output object so you can chain multiple function calls on it
         */
        public Output setEmotion(RoboyEmotion emotion) {
            if (type == OutputType.USE_FALLBACK) {
                logger.warn("Adding a emotion to an answer that requires fallback is not allowed! " +
                        "Emotion behaviour is defined in the fallback state.");
            }
            this.emotion = emotion;
            return this;
        }
        public boolean hasEmotion() { return emotion != null; }
        public RoboyEmotion getEmotion() { return emotion; }

    }

    //endregion



    // #####################################################
    // #            start of state implementation          #
    // #####################################################

    //region variables & constructor

    private final Logger logger = LogManager.getLogger();

    // State name/identifier
    private String stateIdentifier;

    // State parameters: contain parameters & references to important objects (RosMainNode, DialogStateMachine)
    private StateParameters parameters;

    // If this state can't react to the input, the Personality state machine will ask the fallback state
    private State fallback;

    // Possible transitions to other states. The next state is selected based on some conditions in getNextState();
    private HashMap<String, State> transitions;

    /**
     * Personality file additional information: everything like state comment goes here.
     * [!!] Do not use it in your state code! This info is only stored to make sure we don't
     *      lose the comment etc. when saving this state to file.
     */
    private HashMap<String, String> optionalPersFileInfo;


    /**
     * Create a state object with given identifier (state name) and parameters.
     *
     * The parameters should contain a reference to a state machine for later use.
     * The state will not automatically add itself to the state machine.
     *
     * @param stateIdentifier  identifier (name) of this state
     * @param params parameters for this state, should contain a reference to a state machine
     */
    public State(String stateIdentifier, StateParameters params) {
        this.stateIdentifier = stateIdentifier;
        fallback = null;
        transitions = new HashMap<>();
        optionalPersFileInfo = new HashMap<>();
        parameters = params;

        if (parameters == null) {
            logger.warn("StateParameters missing in the State constructor!");
        }
    }

    //endregion

    //region getter & setter for identifier, parameters, fallback & transitions

    public String getIdentifier() {
        return stateIdentifier;
    }
    public void setIdentifier(String stateIdentifier) {
        this.stateIdentifier = stateIdentifier;
    }

    public StateParameters getParameters() {
        return parameters;
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


    /**
     * Set personality file additional information like state comment.
     * [!!] Do not use it in your state code! This info is only stored to make sure we don't
     *      lose the comment etc. when saving this state to file.
     */
    public final void setOptionalPersFileInfo(String key, String value) {
        optionalPersFileInfo.put(key, value);
    }
    /**
     * Get personality file additional information like state comment.
     * [!!] Do not use it in your state code! This info is only stored to make sure we don't
     *      lose the comment etc. when saving this state to file.
     */
    public final String getOptionalPersFileInfo(String key) {
        return optionalPersFileInfo.get(key);
    }

    //endregion



    // #####################################################
    // # functions that MUST be implemented in sub classes #
    // #####################################################

    // region to be implemented in subclasses

    /**
     * A state always acts after the reaction. Both, the reaction of the last and the action of the next state,
     * are combined to give the answer of Roboy.
     * @return interpretations
     */
    public abstract Output act();


    /**
     * Defines how to react to an input. This is usually the answer to the incoming question or some other statement.
     * If this state can't react, it can return 'null' to trigger the fallback state for the answer.
     *
     * Note: In the new architecture, react() does not define the next state anymore! Reaction and state
     * transitions are now decoupled. State transitions are defined in getNextState()
     *
     * @param input input from the person we talk to
     * @return reaction to the input (should not be null)
     */
    public abstract Output react(Interpretation input);


    /**
     * After this state has reacted, the personality state machine will ask this state to which state to go next.
     * If this state is not ready, it will return itself. Otherwise, depending on internal conditions, this state
     * will select one of the states defined in transitions to be the next one.
     *
     * @return next active state after this one has reacted
     */
    public abstract State getNextState();


    //endregion



    // #####################################################
    // #                  utility functions                #
    // #####################################################

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

    protected Set<String> getRequiredParameterNames() {
        return new HashSet<>();
    }


    /**
     * This function can be overridden to sub classes to indicate that this state can require a fallback.
     * If this is the case, but no fallback was defined, you will be warned.
     * @return true if this state requires a fallback and false otherwise
     */
    public boolean isFallbackRequired() {
        return false;
    }

    /**
     * Checks if all required transitions were initialized correctly.
     * Required transitions are defined in getRequiredTransitionNames().
     *
     * @return true if all required transitions of this state were initialized correctly
     */
    public final boolean allRequiredTransitionsAreInitialized() {
        boolean allGood = true;
        for (String tName : getRequiredTransitionNames()) {
            if (!transitions.containsKey(tName)) {
                logger.error("State " + getIdentifier() + ": transition " + tName
                        + " is required but is not defined!");
                allGood = false;
            }
        }
        return allGood;
    }

    /**
     * Checks if all required parameters were initialized correctly.
     * Required parameters are defined in getRequiredParameterNames().
     *
     * @return true if all required parameters of this state were initialized correctly
     */
    public final boolean allRequiredParametersAreInitialized() {
        if (parameters == null) {
            logger.error("State " + getIdentifier() + ": parameters are missing completely!");
            return false;
        }
        if (parameters.getStateMachine() == null) {
            logger.error("State " + getIdentifier() + ": reference to the state machine is missing in the parameters!");
            return false;
        }

        boolean allGood = true;
        for (String paramName : getRequiredParameterNames()) {
            if (parameters.getParameter(paramName) == null) {
                logger.error("State " + getIdentifier() + ": parameter " + paramName
                        + " is required but is not defined!");
                allGood = false;
            }
        }
        return allGood;
    }

    //endregion

    //region shortcuts to create new string sets and access state machine & ros main node
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

    /**
     * Shortcut for getParameters().getStateMachine()
     * @return DialogStateMachine
     */
    protected DialogStateMachine getStateMachine() {
        if (getParameters() == null) return null;
        return getParameters().getStateMachine();
    }

    /**
     * Shortcut for getParameters().getRosMainNode()
     * @return RosMainNode (if previously provided to the DialogStateMachine)
     */
    protected RosMainNode getRosMainNode() {
        if (getParameters() == null) {
            logger.error("RosNode is null");
            return null;
        }
        return getParameters().getRosMainNode();
    }

    /**
     * Shortcut for getParameters().getMemory()
     * @return Neo4jMemoryInterface (if previously provided to the DialogStateMachine)
     */
    protected Neo4jMemoryInterface getMemory() {
        if (getParameters() == null) return null;
        return getParameters().getMemory();
    }

    /**
     * Shortcut for getParameters().getInference()
     * @return InferenceEngine
     */
    protected InferenceEngine getInference() {
        if (getParameters() == null) return null;
        return getParameters().getInference();
    }

    /**
     * Shortcut for getParameters().getStateMachine().getContext()
     * @return Context
     */
    protected Context getContext() {
        if (getParameters() == null) return null;
        return getParameters().getStateMachine().getContext();
    }


    //endregion



    // #####################################################
    // #            to string, to json, equals             #
    // #####################################################

    //region to string & to json

    /**
     * Create a JSON representation for this state. Only the identifier,
     * class name, transitions, parameters and fallback identifier are saved.
     * Internal other internal variables are ignored.
     * @return JSON representation for this state
     */
    public JsonObject toJsonObject() {
        JsonObject stateJson = new JsonObject();
        stateJson.addProperty("identifier", getIdentifier());

        String className = getClass().getCanonicalName();
        stateJson.addProperty("implementation", className);

        if (fallback != null) {
            String fallbackID = fallback.getIdentifier();
            stateJson.addProperty("fallback", fallbackID);
        } else {
            stateJson.add("fallback", JsonNull.INSTANCE);
        }


        // transitions
        JsonObject transitionsJson = new JsonObject();
        for (Map.Entry<String, State> transition : getAllTransitions().entrySet()) {
            String transName = transition.getKey();
            String transStateID = transition.getValue().getIdentifier();
            transitionsJson.addProperty(transName, transStateID);
        }
        stateJson.add("transitions", transitionsJson);

        // parameters
        if (getParameters() == null) return stateJson;


        JsonObject parametersJson = new JsonObject();
        for (Map.Entry<String, String> parameter : getParameters().getAllParameters().entrySet()) {
            String paramName = parameter.getKey();
            String paramValue = parameter.getValue();
            parametersJson.addProperty(paramName, paramValue);
        }
        stateJson.add("parameters", parametersJson);

        // optional personality file info: state comment
        String stateComment = getOptionalPersFileInfo("comment");
        if (stateComment != null) {
            stateJson.addProperty("comment", stateComment);
        }


        return stateJson;

    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("State ").append(getIdentifier()).append(" of class ");
        s.append(this.getClass().getSimpleName()).append(" {\n");

        State fallback = getFallback();
        if (fallback != null) {
            s.append("  [Fallback]   state: ").append(fallback.getIdentifier()).append("\n");
        }
        for (Map.Entry<String, State> transition : getAllTransitions().entrySet()) {
            s.append("  [Transition] ").append(transition.getKey()).append(": ");
            s.append(transition.getValue().getIdentifier()).append("\n");
        }
        if (getParameters() != null) {
            for (Map.Entry<String, String> parameter : getParameters().getAllParameters().entrySet()) {
                s.append("  [Parameter]  ").append(parameter.getKey()).append(": ");
                s.append(parameter.getValue()).append("\n");
            }
        }

        return s.append("}\n").toString();


    }

    //endregion

    //region equals

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

    //endregion

    /**
     * Helper function
     * Ask memory to return nodes for given ids
     * @param ids ids for memory
     * @return Collection of MemoryNodeModels
     */
    protected RandomList<MemoryNodeModel> getMemNodesByIds(ArrayList<Integer> ids) {
        RandomList<MemoryNodeModel> retrievedNodes = new RandomList<>();

        if (ids != null && !ids.isEmpty()) {
            try {
                Gson gson = new Gson();
                for (Integer id : ids) {
                    String requestedObject = getMemory().getById(id);
                    retrievedNodes.add(gson.fromJson(requestedObject, MemoryNodeModel.class));
                }
            } catch (InterruptedException | IOException e) {
                logger.error("Error on Memory data retrieval: " + e.getMessage());
            }
        }

        return retrievedNodes;
    }

}