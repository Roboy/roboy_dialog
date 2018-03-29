package roboy.dialog;

import roboy.dialog.tutorials.toyStates.ToyFarewellState;
import roboy.dialog.tutorials.toyStates.ToyGreetingsState;
import roboy.dialog.states.definitions.StateParameters;

/**
 * Helper class for testing: creates a minimal state machine with 2 states.
 */
public class MiniTestStateMachineCreator {

    /**
     * Returns a String representation of a minimal state machine with only two states.
     * The representation is equal to the machine from getMiniStateMachine().
     *
     * @return String representation of a minimal state machine with only two states
     */
    public static String getMiniStateMachineString() {
        return "{\n" +
                "  \"initialState\": \"Greetings\",\n" +
                "  \"states\": [\n" +
                "    {\n" +
                "      \"identifier\": \"Farewell\",\n" +
                "      \"implementation\": \"roboy.dialog.tutorials.toyStates.ToyFarewellState\",\n" +
                "      \"transitions\": {},\n" +
                "      \"parameters\": {\n" +
                "        \"PARAMETER_NAME\": \"PARAMETER_VALUE\"\n" +
                "      }\n"+
                "    },\n" +
                "    {\n" +
                "      \"identifier\": \"Greetings\",\n" +
                "      \"implementation\": \"roboy.dialog.tutorials.toyStates.ToyGreetingsState\",\n" +
                "      \"fallback\": \"Farewell\",\n" +
                "      \"transitions\": {\n" +
                "        \"next\": \"Farewell\",\n" +
                "        \"noHello\": \"Farewell\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }

    /**
     * Creates a minimal state machine with only two states from code.
     * The machine is equal to the String representation from getMiniStateMachineString().
     * @return a minimal state machine with only two states created from code
     */
    public static DialogStateMachine getMiniStateMachine() {

        DialogStateMachine machine = new DialogStateMachine();

        StateParameters paramsGreetings = new StateParameters(machine);
        paramsGreetings.setParameter("PARAMETER_NAME", "PARAMETER_VALUE");
        StateParameters paramsFarewell = new StateParameters(machine);

        ToyGreetingsState greeting = new ToyGreetingsState("Greetings", paramsGreetings);
        ToyFarewellState farewell = new ToyFarewellState("Farewell", paramsFarewell);

        greeting.setTransition("next", farewell);
        greeting.setTransition("noHello", farewell);
        greeting.setFallback(farewell);

        machine.addState(greeting);
        machine.addState(farewell);
        machine.setInitialState(greeting);

        return machine;
    }



}
