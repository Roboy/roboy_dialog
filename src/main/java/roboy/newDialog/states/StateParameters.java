package roboy.newDialog.states;

import roboy.newDialog.DialogStateMachine;

import java.util.HashMap;

/**
 * An object containing all parameters that can be interesting for an arbitrary State object.
 */
public class StateParameters {

    // TODO: references to context, ros node, ...

    private HashMap<String, String> paramNameToValue;
    private DialogStateMachine stateMachine;

    public StateParameters(DialogStateMachine stateMachine) {
        paramNameToValue = new HashMap<>();
        this.stateMachine = stateMachine;

        if (stateMachine == null) {
            System.err.println("[!!] StateParameters require a reference to the state machine");
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
