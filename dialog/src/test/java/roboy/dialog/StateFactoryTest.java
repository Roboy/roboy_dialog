package roboy.dialog;

import org.junit.Test;
import roboy.context.Context;
import roboy.dialog.tutorials.tutorialStates.*;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateFactory;
import roboy.dialog.states.definitions.StateParameters;
import roboy.logic.Inference;
import roboy.logic.InferenceEngine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Tests related to the StateFactory.
 */
public class StateFactoryTest {


    // create an instance of every toy state using the StateFactory
    @Test
    public void factoryCreatesCorrectStateObjects() {
        ArrayList<Class<? extends State>> classes = new ArrayList<>();
        classes.add(ToyFarewellState.class);
        classes.add(ToyGreetingsState.class);
        classes.add(ToyRandomAnswerState.class);
        classes.add(ToyIntroState.class);


        InferenceEngine inference = new Inference();
        Context context = new Context();
        StateParameters parms = new StateParameters(new DialogStateMachine(inference, context));
        parms.setParameter("introductionSentence", "some magic here");
        for (Class<? extends State> cls : classes) {
            String className = cls.getCanonicalName();
            String stateID = cls.getSimpleName();
            State result = StateFactory.createStateByClassName(className, stateID, parms);

            assertTrue(cls.isInstance(result));
        }
    }

    // feed some evil stuff into the factory and make sure it doesn't break
    @Test
    public void factoryDoesNotBreakOnInvalidClassNames() {
        // make sure the produced error output doesn't go to the console (would cause confusion)
        PrintStream serr = System.err; // save System.err for later
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        StateFactory.createStateByClassName(null, null, null);
        StateFactory.createStateByClassName("invalid class name", null, null);
        StateFactory.createStateByClassName("java.lang.String", null, null);

        // restore System.err
        System.setErr(serr);
    }

}
