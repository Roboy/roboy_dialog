package roboy.newDialog.states.factories;


import roboy.newDialog.states.State;
import roboy.newDialog.states.StateParameters;

/**
 * Temporary factory to create State objects based on class name.
 *
 * May be replaced with something more generic later.
 */
public class ToyStateFactory {

    public static State getByClassName(String className, String instanceName, StateParameters parameters) {
        switch (className) {
            case "roboy.newDialog.states.toyStates.ToyFarewellState":
                return new roboy.newDialog.states.toyStates.ToyFarewellState(instanceName, parameters);
            case "roboy.newDialog.states.toyStates.ToyGreetingsState":
                return new roboy.newDialog.states.toyStates.ToyGreetingsState(instanceName, parameters);
            case "roboy.newDialog.states.toyStates.ToyIntroState":
                return new roboy.newDialog.states.toyStates.ToyIntroState(instanceName, parameters);
            case "roboy.newDialog.states.toyStates.ToyRandomAnswerState":
                return new roboy.newDialog.states.toyStates.ToyRandomAnswerState(instanceName, parameters);
            default:
                System.out.println("Unknown class name: " + className);
                return null;
        }
    }

}
