package roboy.dialog.personality.experimental.toyStates;


import roboy.dialog.personality.experimental.AbstractState;

/**
 * Temporary factory to create State objects based on class name.
 *
 * May be replaced with something more generic later.
 */
public class ToyStateFactory {

    public static AbstractState getByClassName(String className, String instanceName) {
        switch (className) {
            case "roboy.dialog.personality.experimental.toyStates.ToyFarewellState":
                return new roboy.dialog.personality.experimental.toyStates.ToyFarewellState(instanceName);
            case "roboy.dialog.personality.experimental.toyStates.ToyGreetingsState":
                return new roboy.dialog.personality.experimental.toyStates.ToyGreetingsState(instanceName);
            case "roboy.dialog.personality.experimental.toyStates.ToyIntroState":
                return new roboy.dialog.personality.experimental.toyStates.ToyIntroState(instanceName);
            case "roboy.dialog.personality.experimental.toyStates.ToyRandomAnswerState":
                return new roboy.dialog.personality.experimental.toyStates.ToyRandomAnswerState(instanceName);
            default:
                return null;
        }
    }

}
