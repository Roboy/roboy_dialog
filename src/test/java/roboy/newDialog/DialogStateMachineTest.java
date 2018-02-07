package roboy.newDialog;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import roboy.newDialog.states.State;
import roboy.newDialog.states.StateParameters;
import roboy.newDialog.examples.toyStates.ToyFarewellState;
import roboy.newDialog.examples.toyStates.ToyGreetingsState;

public class DialogStateMachineTest {


    // minimal state machine with 2 states
    private static String MINI_STATE_MACHINE = "{\n" +
            "  \"initialState\": \"Greetings\",\n" +
            "  \"states\": [\n" +
            "    {\n" +
            "      \"identifier\": \"Farewell\",\n" +
            "      \"implementation\": \"roboy.newDialog.examples.toyStates.ToyFarewellState\",\n" +
            "      \"transitions\": {}\n" +
            "    },\n" +
            "    {\n" +
            "      \"identifier\": \"Greetings\",\n" +
            "      \"implementation\": \"roboy.newDialog.examples.toyStates.ToyGreetingsState\",\n" +
            "      \"fallback\": \"Farewell\",\n" +
            "      \"transitions\": {\n" +
            "        \"next\": \"Farewell\",\n" +
            "        \"noHello\": \"Farewell\"\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    // helper, creates equivalent state machine from code
    private static DialogStateMachine fromCode() {

        DialogStateMachine machine = new DialogStateMachine();
        StateParameters params = new StateParameters(machine);

        ToyGreetingsState greeting = new ToyGreetingsState("Greetings", params);
        ToyFarewellState farewell = new ToyFarewellState("Farewell", params);
        greeting.setTransition("next", farewell);
        greeting.setTransition("noHello", farewell);
        greeting.setFallback(farewell);
        machine.addState(greeting);
        machine.addState(farewell);
        machine.setInitialState(greeting);

        return machine;
    }


    // machine should always equal itself
    @Test
    public void machineEqualsItself() {
        DialogStateMachine machine = new DialogStateMachine();
        assertTrue(machine.equals(machine));

        machine.loadFromString(MINI_STATE_MACHINE);
        assertTrue(machine.equals(machine));
    }

    // minimal string example should equal the machine build from code
    @Test
    public void stringEqualsCode() {
        DialogStateMachine fromString = new DialogStateMachine();
        fromString.loadFromString(MINI_STATE_MACHINE);

        DialogStateMachine fromCode = fromCode();

        assertTrue(fromString.equals(fromCode));
        assertTrue(fromCode.equals(fromString));
    }

    // machines are not equal if initial state is different
    @Test
    public void notEqualsNoInitialState() {
        DialogStateMachine fromString = new DialogStateMachine();
        fromString.loadFromString(MINI_STATE_MACHINE);

        DialogStateMachine fromCode = fromCode();
        // change initial state
        fromCode.setInitialState(fromCode.getStateByIdentifier("Farewell"));

        assertFalse(fromString.equals(fromCode));
        assertFalse(fromCode.equals(fromString));
    }

    // machines are not equal if one has more states
    @Test
    public void notEqualsDifferentStates() {
        DialogStateMachine fromString = new DialogStateMachine();
        fromString.loadFromString(MINI_STATE_MACHINE);

        DialogStateMachine fromCode = fromCode();
        // add one more state
        fromCode.addState(new ToyGreetingsState("NewEvilState", new StateParameters(fromString)));

        assertFalse(fromString.equals(fromCode));
        assertFalse(fromCode.equals(fromString));
    }

    // machines are not equal if one has different transitions
    @Test
    public void notEqualsDifferentTransitions() {
        DialogStateMachine fromString = new DialogStateMachine();
        fromString.loadFromString(MINI_STATE_MACHINE);

        DialogStateMachine fromCode = fromCode();
        // changeTransitions
        State greetingsCode = fromCode.getStateByIdentifier("Greetings");
        greetingsCode.setTransition("evilLoopback", greetingsCode);

        assertFalse(fromString.equals(fromCode));
        assertFalse(fromCode.equals(fromString));
    }


    // after loading, the initial set equals the active state
    @Test
    public void activeStateIsSetToInitialState() {
        DialogStateMachine machine = new DialogStateMachine();
        machine.loadFromString(MINI_STATE_MACHINE);

        assertTrue(machine.getInitialState() == machine.getActiveState());
    }

    // all states from the string are present in the machine
    @Test
    public void machineContainsAllStates() {
        DialogStateMachine machine = new DialogStateMachine();
        machine.loadFromString(MINI_STATE_MACHINE);
        assertTrue( machine.getStateByIdentifier("Greetings") != null );
        assertTrue( machine.getStateByIdentifier("Farewell") != null );
    }


    // all transitions from the string are present in the machine
    @Test
    public void transitionsAreOK() {
        DialogStateMachine machine = new DialogStateMachine();
        machine.loadFromString(MINI_STATE_MACHINE);
        State greetings = machine.getStateByIdentifier("Greetings");
        State farewell = machine.getStateByIdentifier("Farewell");

        assertTrue( greetings.getTransition("noHello") == farewell);
        assertTrue( greetings.getTransition("next") == farewell );
    }

    // all fallbacks from the string are present in the machine
    @Test
    public void fallbackIsOK() {
        DialogStateMachine machine = new DialogStateMachine();
        machine.loadFromString(MINI_STATE_MACHINE);
        State greetings = machine.getStateByIdentifier("Greetings");
        State farewell = machine.getStateByIdentifier("Farewell");

        assertTrue( greetings.getFallback() == farewell);
    }





}
