package roboy.io;

import java.util.List;

import roboy.dialog.action.Action;

public class EmotionOutputDevice implements OutputDevice{

	@Override
	public void act(List<Action> actions) {
		System.out.println("*Blush*");
	}

}
