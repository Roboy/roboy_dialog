package roboy.newDialog.states;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.newDialog.DialogStateMachine;

import java.util.HashMap;

/**
 * An object containing all parameters that can be interesting for an arbitrary State object.
 */
public class StateParameters {

    private final Logger logger = LogManager.getLogger();

    // TODO: references to context, ros node, ...

    private HashMap<String, String> paramNameToValue;
    private DialogStateMachine stateMachine;

    public StateParameters(DialogStateMachine stateMachine) {
        paramNameToValue = new HashMap<>();
        this.stateMachine = stateMachine;

        if (stateMachine == null) {
            logger.error("StateParameters require a reference to the state machine");
        }

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



}
