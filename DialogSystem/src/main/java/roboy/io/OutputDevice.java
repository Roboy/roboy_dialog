package roboy.io;

import java.util.List;

import roboy.dialog.action.Action;

/**
 * An output device gets a list of actions and should perform those that it can handle.
 */
public interface OutputDevice {
	public void act(List<Action> actions);
}
