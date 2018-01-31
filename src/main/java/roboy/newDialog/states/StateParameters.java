package roboy.newDialog.states;

import java.util.HashMap;

/**
 * An object containing all parameters that can be interesting for an arbitrary State object.
 */
public class StateParameters {

    // TODO: references to context, ros node, state machine, ...

    private HashMap<String, String> paramNameToValue;

    public StateParameters() {
        paramNameToValue = new HashMap<>();
    }

    public StateParameters set(String parameterName, String value) {
        paramNameToValue.put(parameterName, value);
        return this;
    }

    public String get(String parameterName) {
        return paramNameToValue.get(parameterName);
    }



}
