package roboy.io;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.json.Json;
import javax.json.JsonObject;

import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Service;
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import edu.wpi.rail.jrosbridge.services.ServiceResponse;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import edu.wpi.rail.jrosbridge.Topic;

import roboy.util.Concept;
import roboy.util.Relation;

public class Vision 
{
	private Ros ros;

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


	public Vision(Ros ros_)
	{
		this.ros = ros_;
	}

	public String recognize()
	{
		Service Recognize = new Service(ros, "/roboy_vision/recognize", "/roboy_face/recognize");

		JsonObject params = Json.createObjectBuilder()
	     .add("object_id", 0) //TODO send actual id
	     .build();
	    
	    ServiceRequest request = new ServiceRequest(params);
	    ServiceResponse response = Recognize.callServiceAndWait(request);

	    return response.toJsonObject().getString("object_name");
	}

	public boolean findFaces()
	{
		// Topic t = new Topic(ros, "/test", "geometry_msgs/Pose");
		// VisionCallback cb = new VisionCallback();
		// t.subscribe(cb);

//		Service Recognize = new Service(ros, "/roboy_vision/find_face", "/roboy_face/find_face");
//
//	    ServiceRequest request = new ServiceRequest();
//	    ServiceResponse response = Recognize.callServiceAndWait(request);

	    //return response.toJsonObject().getBoolean("face_detected");
		return true;
	}

}