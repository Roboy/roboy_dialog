package roboy.dialog;

import org.junit.Test;
import roboy.dialog.states.definitions.State;

import static org.junit.Assert.*;

/**
 * Tests related to the state machine initialization and loading from file/string.
 */
public class StateMachineInitializationTest {

    // after loading, the initial state equals the active state
    @Test
    public void activeStateIsSetToInitialState() {
        DialogStateMachine machine = new DialogStateMachine();
        machine.loadFromString(MiniTestStateMachineCreator.getMiniStateMachineString());

        assertTrue(machine.getInitialState() == machine.getActiveState());
    }


    // all states from the string are present in the machine
    @Test
    public void machineContainsAllStates() {
        DialogStateMachine machine = new DialogStateMachine();
        machine.loadFromString(MiniTestStateMachineCreator.getMiniStateMachineString());
        assertTrue( machine.getStateByIdentifier("Greetings") != null );
        assertTrue( machine.getStateByIdentifier("Farewell") != null );
    }


    // all transitions from the string are present in the machine
    @Test
    public void transitionsAreOK() {
        DialogStateMachine machine = new DialogStateMachine();
        machine.loadFromString(MiniTestStateMachineCreator.getMiniStateMachineString());
        State greetings = machine.getStateByIdentifier("Greetings");
        State farewell = machine.getStateByIdentifier("Farewell");

        assertTrue( greetings.getTransition("noHello") == farewell);
        assertTrue( greetings.getTransition("next") == farewell );
    }


    // all parameters from the string are present in the machine
    @Test
    public void parametersAreOK() {
        DialogStateMachine machine = new DialogStateMachine();
        machine.loadFromString(MiniTestStateMachineCreator.getMiniStateMachineString());
        State farewell = machine.getStateByIdentifier("Farewell");

        assertEquals("PARAMETER_VALUE", farewell.getParameters().getParameter("PARAMETER_NAME"));
    }


    // all fallbacks from the string are present in the machine
    @Test
    public void fallbackIsOK() {
        DialogStateMachine machine = new DialogStateMachine();
        machine.loadFromString(MiniTestStateMachineCreator.getMiniStateMachineString());
        State greetings = machine.getStateByIdentifier("Greetings");
        State farewell = machine.getStateByIdentifier("Farewell");

        assertTrue( greetings.getFallback() == farewell);
    }


}
