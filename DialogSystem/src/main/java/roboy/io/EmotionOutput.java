package roboy.io;

import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;

import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Service;
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import edu.wpi.rail.jrosbridge.services.ServiceResponse;

import roboy.dialog.action.Action;
import roboy.dialog.action.FaceAction;

public class EmotionOutput implements OutputDevice 
{
	private Ros ros;
	
	public EmotionOutput(Ros ros_)
	{
		this.ros = ros_;
	}
	
	@Override
	public void act(List<Action> actions) 
	{
		for(Action a : actions)
		{
			if(a instanceof FaceAction)
			{
				ChangeFaceState((FaceAction) a);
			}
		}
	}
	
	private void ChangeFaceState(FaceAction action)
	{
	    Service FaceState = new Service(ros, "/roboy_face/change_state", "/roboy_face/change_state");
	    
	    JsonObject params = Json.createObjectBuilder()
	     .add("action", action.getState())
	     .add("duration", action.getDuration())
	     .build();

	    System.out.println("Face state: " + action.getState());

	    // ServiceRequest request = new ServiceRequest(params);
	    // FaceState.callServiceAndWait(request);
	}
	
}
