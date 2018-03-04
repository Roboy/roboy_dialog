package roboy.newDialog.states;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.newDialog.DialogStateMachine;
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

    // TODO: references to context, ros node, ...

    private final HashMap<String, String> paramNameToValue;
    private final DialogStateMachine stateMachine;
    private final RosMainNode rosMainNode;

    public StateParameters(DialogStateMachine stateMachine, RosMainNode rmn) {
        paramNameToValue = new HashMap<>();
        this.stateMachine = stateMachine;
        this.rosMainNode = rmn;

        if (stateMachine == null) {
            logger.warn("StateParameters should have a reference to the state machine");
        }

        if (rosMainNode == null) {
            logger.info("Using offline StateParameters (no RosMainNode passed)");
        }
    }

    public StateParameters(DialogStateMachine stateMachine) {
        this(stateMachine, null);
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
}
