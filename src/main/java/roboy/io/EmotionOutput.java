package roboy.io;

import java.util.List;

import roboy.dialog.action.Action;
import roboy.dialog.action.FaceAction;
import roboy.ros.RosMainNode;

/**
 * Roboy's facial expression output.
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
			if (a instanceof FaceAction) {
				System.out.print(((FaceAction) a).getState());
				rosMainNode.ShowEmotion(((FaceAction) a).getState());
			}
		}
	}

	public void act(Action action) {
		if (action instanceof FaceAction) {
			if (((FaceAction) action).getDuration()>0)
			{
				System.out.print(((FaceAction) action).getState());
				rosMainNode.ShowEmotion(((FaceAction) action).getState());
			}

		}
	}

}
