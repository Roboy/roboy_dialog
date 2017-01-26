package roboy.io;

import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Service;
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import edu.wpi.rail.jrosbridge.services.ServiceResponse;
import org.json.*;

public class BingInput implements InputDevice 
{
	private Ros ros;
	
	public BingInput()
	{
		ros = new Ros("localhost");
	    ros.connect();
	    System.out.println("ROS bridge is set up");	
	}
	@Override
	public String listen() throws InterruptedException 
	{
	    Service BingSTT = new Service(ros, "TextSpoken", "TextSpoken");
	    ServiceRequest request = new ServiceRequest("{}");
//	    ServiceRequest request = new ServiceRequest("{\"text\": \"eat a dick java!\"}");
	    System.out.println("Sending TTS request");	
	    ServiceResponse response = BingSTT.callServiceAndWait(request);
	    
	    String input = response.toString();
	    
	    JSONObject obj = new JSONObject(input);
	    String text = obj.getString("text");
	    
	    System.out.println(text);
		return text;
	}
	
}
