package roboy.io;

import java.util.List;

import roboy.dialog.action.Action;

public interface OutputDevice {
	public void act(List<Action> actions);
}
