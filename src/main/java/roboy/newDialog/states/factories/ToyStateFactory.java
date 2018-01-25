package roboy.newDialog.states.factories;


import roboy.newDialog.states.State;

/**
 * Temporary factory to create State objects based on class name.
 *
 * May be replaced with something more generic later.
 */
public class ToyStateFactory {

    public static State getByClassName(String className, String instanceName) {
        switch (className) {
            case "roboy.newDialog.states.toyStates.ToyFarewellState":
                return new roboy.newDialog.states.toyStates.ToyFarewellState(instanceName);
            case "roboy.newDialog.states.toyStates.ToyGreetingsState":
                return new roboy.newDialog.states.toyStates.ToyGreetingsState(instanceName);
            case "roboy.newDialog.states.toyStates.ToyIntroState":
                return new roboy.newDialog.states.toyStates.ToyIntroState(instanceName);
            case "roboy.newDialog.states.toyStates.ToyRandomAnswerState":
                return new roboy.newDialog.states.toyStates.ToyRandomAnswerState(instanceName);
            default:
                System.out.println("Unknown class name: " + className);
                return null;
        }
    }

}
