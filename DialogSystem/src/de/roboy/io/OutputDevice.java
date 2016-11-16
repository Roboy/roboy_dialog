package de.roboy.io;

import java.util.List;

import de.roboy.dialog.action.Action;

public interface OutputDevice {
	public void act(List<Action> actions);
}
