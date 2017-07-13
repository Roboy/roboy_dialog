package roboy.io;

import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;

import edu.wpi.rail.jrosbridge.Service;
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import edu.wpi.rail.jrosbridge.services.ServiceResponse;

import roboy.dialog.action.Action;
import roboy.dialog.action.FaceAction;
import roboy.util.Ros;
import roboy.util.RosMainNode;

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
