package org.roboy.io;

import java.util.List;

import org.roboy.dialog.action.Action;
import org.roboy.dialog.action.EmotionAction;
import org.roboy.ros.RosMainNode;

/**
 * RoboyModel's facial expression output.
 */
public class EmotionOutput implements OutputDevice 
{
	private RosMainNode rosMainNode;

	public EmotionOutput (RosMainNode node){
		this.rosMainNode = node;
	}

	@Override
	public void act(List<Action> actions) {
		for (Action a : actions) {
			if (a instanceof EmotionAction) {
				System.out.print(((EmotionAction) a).getState());
				rosMainNode.ShowEmotion(((EmotionAction) a).getState());
			}
		}
	}

	public void act(Action action) {
		if (action instanceof EmotionAction) {
			if (((EmotionAction) action).getDuration()>0)
			{
				System.out.print(((EmotionAction) action).getState());
				rosMainNode.ShowEmotion(((EmotionAction) action).getState());
			}

		}
	}

}
