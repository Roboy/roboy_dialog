package roboy.dialog;

import roboy.context.Context;
import roboy.dialog.tutorials.tutorialStates.ToyFarewellState;
import roboy.dialog.tutorials.tutorialStates.ToyGreetingsState;
import roboy.dialog.states.definitions.StateParameters;
import roboy.logic.Inference;
import roboy.logic.InferenceEngine;

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
                "      \"implementation\": \"roboy.dialog.tutorials.tutorialStates.ToyFarewellState\",\n" +
                "      \"transitions\": {},\n" +
                "      \"parameters\": {\n" +
                "        \"PARAMETER_NAME\": \"PARAMETER_VALUE\"\n" +
                "      }\n"+
                "    },\n" +
                "    {\n" +
                "      \"identifier\": \"Greetings\",\n" +
                "      \"implementation\": \"roboy.dialog.tutorials.tutorialStates.ToyGreetingsState\",\n" +
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

        InferenceEngine inference = new Inference();
        Context context = new Context();
        DialogStateMachine machine = new DialogStateMachine(inference, context);

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
