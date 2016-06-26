package de.roboy.dialog.personality;

import java.util.List;

import de.roboy.dialog.action.Action;

public interface Personality {

	public List<Action> answer(String input);
}
