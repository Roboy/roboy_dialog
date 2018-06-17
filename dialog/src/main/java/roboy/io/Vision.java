package roboy.io;

import javax.json.Json;
import javax.json.JsonObject;

import edu.wpi.rail.jrosbridge.Service;
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;

import roboy.ros.Ros;

/**
 * Vision helper class
 */
public class Vision 
{

	private static Vision roboyVision;

	private Vision()
	{
	}

	public static Vision getInstance()
	{
		if (roboyVision == null)
		{
			roboyVision =  new Vision();
		}
		return roboyVision;
	}
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


	public String recognizeFace()
	{
		Service RecognizeSrv = new Service(Ros.getInstance(), "/recognize_face", "/recognize_face");
		JsonObject params = Json.createObjectBuilder()
				.add("object_id", 0)
				.build();
		ServiceRequest request = new ServiceRequest(params);
		return RecognizeSrv.callServiceAndWait(request).toJsonObject().getString("object_name");
	}

	public boolean findFaces()
	{
		Service RecognizeSrv = new Service(Ros.getInstance(), "/detect_face", "/detect_face");
		ServiceRequest request = new ServiceRequest();
		return RecognizeSrv.callServiceAndWait(request).toJsonObject().getBoolean("face_detected");
	}

}