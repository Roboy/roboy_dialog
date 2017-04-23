package roboy.io;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.json.Json;
import javax.json.JsonObject;

import edu.wpi.rail.jrosbridge.Service;
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import edu.wpi.rail.jrosbridge.services.ServiceResponse;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import edu.wpi.rail.jrosbridge.Topic;

import roboy.util.Concept;
import roboy.util.Relation;
import roboy.util.Ros;

/**
 * Vision helper class ... I guess.
 */
public class Vision 
{


	private class VisionCallback implements TopicCallback {

		public String latest = null;
		public boolean faceDetected = false;

		public void handleMessage(Message message) 
		{
			// System.out.println(message.toJsonObject().getString("position").toString());
			System.out.println(message.toString());
			
			// this.latest = message.toString();
			// JsonObject msg = message.toJsonObject();
			// if (message.toJsonObject().getString("position") != "")
			// {
			// 	this.faceDetected = true;
			// }
			// System.out.println(message.toString());
			// System.out.println(message.toJsonObject().getString("position").toString());
			// System.out.println(Thread.currentThread().isAlive());
			
		}
	}



	public String recognize()
	{
		Service Recognize = new Service(Ros.getInstance(), "/roboy_vision/recognize", "/roboy_face/recognize");

		JsonObject params = Json.createObjectBuilder()
	     .add("object_id", 0) //TODO send actual id
	     .build();
	    
	    ServiceRequest request = new ServiceRequest(params);
	    ServiceResponse response = Recognize.callServiceAndWait(request);

	    return response.toJsonObject().getString("object_name");
	}

	public boolean findFaces()
	{
		Service RecognizeSrv = new Service(Ros.getInstance(), "/detect_face", "/detect_face");
		ServiceRequest request = new ServiceRequest();
		return RecognizeSrv.callServiceAndWait(request).toJsonObject().getBoolean("face_detected");
	}

}