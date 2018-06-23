package roboy.dialog;

import org.junit.Test;
import roboy.context.Context;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.dialog.tutorials.tutorialStates.ToyGreetingsState;
import roboy.logic.Inference;

import static org.junit.Assert.*;

/**
 * Tests related to state machine equality.
 */
public class StateMachineEqualityTest {


    // machine should always equal itself
    @Test
    public void machineEqualsItself() {
        DialogStateMachine machine = new DialogStateMachine(new Inference(), new Context());
        assertEquals(machine, machine);

        machine.loadFromString(MiniTestStateMachineCreator.getMiniStateMachineString());
        assertEquals(machine, machine);
    }


    // minimal string example should equal the machine build from code
    @Test
    public void stringEqualsCode() {
        DialogStateMachine fromString = new DialogStateMachine(new Inference(), new Context());
        fromString.loadFromString(MiniTestStateMachineCreator.getMiniStateMachineString());

        DialogStateMachine fromCode = MiniTestStateMachineCreator.getMiniStateMachine();

        assertTrue(fromString.equals(fromCode));
        assertTrue(fromCode.equals(fromString));
    }


    // machines are not equal if initial state is different
    @Test
    public void notEqualsNoInitialState() {
        DialogStateMachine fromString = new DialogStateMachine(new Inference(), new Context());
        fromString.loadFromString(MiniTestStateMachineCreator.getMiniStateMachineString());

        DialogStateMachine fromCode = MiniTestStateMachineCreator.getMiniStateMachine();
        // change initial state
        fromCode.setInitialState(fromCode.getStateByIdentifier("Farewell"));

        assertFalse(fromString.equals(fromCode));
        assertFalse(fromCode.equals(fromString));
    }


    // machines are not equal if one has more states
    @Test
    public void notEqualsDifferentStates() {
        Context context = new Context();
        DialogStateMachine fromString = new DialogStateMachine(new Inference(), context);
        fromString.loadFromString(MiniTestStateMachineCreator.getMiniStateMachineString());

        DialogStateMachine fromCode = MiniTestStateMachineCreator.getMiniStateMachine();
        // add one more state
        fromCode.addState(new ToyGreetingsState("NewEvilState", new StateParameters(fromString)));

        assertFalse(fromString.equals(fromCode));
        assertFalse(fromCode.equals(fromString));
    }


    // machines are not equal if one of the corresponding states has different transitions
    @Test
    public void notEqualsDifferentTransitions() {
        Context context = new Context();
        DialogStateMachine fromString = new DialogStateMachine(new Inference(), context);
        fromString.loadFromString(MiniTestStateMachineCreator.getMiniStateMachineString());

        DialogStateMachine fromCode = MiniTestStateMachineCreator.getMiniStateMachine();
        // changeTransitions
        State greetingsCode = fromCode.getStateByIdentifier("Greetings");
        greetingsCode.setTransition("evilLoopback", greetingsCode);

        assertFalse(fromString.equals(fromCode));
        assertFalse(fromCode.equals(fromString));
    }


}
