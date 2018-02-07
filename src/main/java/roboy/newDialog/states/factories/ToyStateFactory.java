package roboy.newDialog.states.factories;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboy.newDialog.states.State;
import roboy.newDialog.states.StateParameters;

/**
 * Temporary factory to create State objects based on class name.
 *
 * May be replaced with something more generic later.
 */
public class ToyStateFactory {

    private final static Logger logger = LoggerFactory.getLogger(ToyStateFactory.class);

    public static State getByClassName(String className, String instanceName, StateParameters parameters) {
        switch (className) {
            case "roboy.newDialog.examples.toyStates.ToyFarewellState":
                return new roboy.newDialog.examples.toyStates.ToyFarewellState(instanceName, parameters);
            case "roboy.newDialog.examples.toyStates.ToyGreetingsState":
                return new roboy.newDialog.examples.toyStates.ToyGreetingsState(instanceName, parameters);
            case "roboy.newDialog.examples.toyStates.ToyIntroState":
                return new roboy.newDialog.examples.toyStates.ToyIntroState(instanceName, parameters);
            case "roboy.newDialog.examples.toyStates.ToyRandomAnswerState":
                return new roboy.newDialog.examples.toyStates.ToyRandomAnswerState(instanceName, parameters);
            default:
                logger.warn("Unknown class name: " + className);
                return null;
        }
    }

}
