package roboy.dialog;

import org.junit.Test;
import roboy.context.Context;
import roboy.dialog.states.definitions.State;
import roboy.logic.Inference;

import static org.junit.Assert.*;

/**
 * Tests related to the state machine initialization and loading from file/string.
 */
public class StateMachineInitializationTest {

    // after loading, the initial state equals the active state
    @Test
    public void activeStateIsSetToInitialState() {
        Context context = new Context();
        DialogStateMachine machine = new DialogStateMachine(new Inference(), context);
        machine.loadFromString(MiniTestStateMachineCreator.getMiniStateMachineString());

        assertTrue(machine.getInitialState() == machine.getActiveState());
    }


    // all states from the string are present in the machine
    @Test
    public void machineContainsAllStates() {
        Context context = new Context();
        DialogStateMachine machine = new DialogStateMachine(new Inference(), context);
        machine.loadFromString(MiniTestStateMachineCreator.getMiniStateMachineString());
        assertTrue( machine.getStateByIdentifier("Greetings") != null );
        assertTrue( machine.getStateByIdentifier("Farewell") != null );
    }


    // all transitions from the string are present in the machine
    @Test
    public void transitionsAreOK() {
        Context context = new Context();
        DialogStateMachine machine = new DialogStateMachine(new Inference(), context);
        machine.loadFromString(MiniTestStateMachineCreator.getMiniStateMachineString());
        State greetings = machine.getStateByIdentifier("Greetings");
        State farewell = machine.getStateByIdentifier("Farewell");

        assertTrue( greetings.getTransition("noHello") == farewell);
        assertTrue( greetings.getTransition("next") == farewell );
    }


    // all parameters from the string are present in the machine
    @Test
    public void parametersAreOK() {
        Context context = new Context();
        DialogStateMachine machine = new DialogStateMachine(new Inference(), context);
        machine.loadFromString(MiniTestStateMachineCreator.getMiniStateMachineString());
        State farewell = machine.getStateByIdentifier("Farewell");

        assertEquals("PARAMETER_VALUE", farewell.getParameters().getParameter("PARAMETER_NAME"));
    }


    // all fallbacks from the string are present in the machine
    @Test
    public void fallbackIsOK() {
        Context context = new Context();
        DialogStateMachine machine = new DialogStateMachine(new Inference(), context);
        machine.loadFromString(MiniTestStateMachineCreator.getMiniStateMachineString());
        State greetings = machine.getStateByIdentifier("Greetings");
        State farewell = machine.getStateByIdentifier("Farewell");

        assertTrue( greetings.getFallback() == farewell);
    }


}
