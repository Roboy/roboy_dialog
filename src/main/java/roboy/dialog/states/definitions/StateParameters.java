package roboy.dialog.states.definitions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.logic.InferenceEngine;
import roboy.memory.Neo4jMemoryInterface;
import roboy.dialog.DialogStateMachine;
import roboy.ros.RosMainNode;

import java.util.HashMap;

/**
 * An object containing all parameters that can be interesting for an arbitrary State object.
 *
 * String parameters are defined in the personality file.
 * Other important parameters are references to the DialogStateMachine and the RosMainNode.
 */
public class StateParameters {

    private final Logger logger = LogManager.getLogger();

    private final HashMap<String, String> paramNameToValue;
    private final DialogStateMachine stateMachine;
    private final RosMainNode rosMainNode;
    private final Neo4jMemoryInterface memory;
    private final InferenceEngine inference;

    public StateParameters(DialogStateMachine stateMachine, RosMainNode rosMainNode, Neo4jMemoryInterface memory, InferenceEngine inference) {
        paramNameToValue = new HashMap<>();
        this.stateMachine = stateMachine;
        this.rosMainNode = rosMainNode;
        this.memory = memory;
        this.inference = inference;

        if (stateMachine == null) {
            logger.debug("StateParameters should have a reference to the state machine");
        }

        if (rosMainNode == null) {
            logger.debug("Using offline StateParameters (no RosMainNode passed)");
        }

        if (memory == null) {
            logger.debug("Using offline StateParameters (no Memory passed)");
        }
    }

    public StateParameters(DialogStateMachine stateMachine, InferenceEngine inference) {
        this(stateMachine, null, null, inference);
    }

    public StateParameters setParameter(String parameterName, String value) {
        paramNameToValue.put(parameterName, value);
        return this;
    }

    public String getParameter(String parameterName) {
        return paramNameToValue.get(parameterName);
    }

    public HashMap<String, String> getAllParameters() {
        return paramNameToValue;
    }

    public DialogStateMachine getStateMachine() {
        return stateMachine;
    }

    public RosMainNode getRosMainNode() {
        return rosMainNode;
    }

    public Neo4jMemoryInterface getMemory() {
        return memory;
    }

    public InferenceEngine getInference() {
        return inference;
    }
}
