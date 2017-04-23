package roboy.io;

import edu.wpi.rail.jrosbridge.services.std.Empty;
import roboy.util.Ros;
import edu.wpi.rail.jrosbridge.Service;
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import edu.wpi.rail.jrosbridge.services.ServiceResponse;
import org.json.*;

/**
 * Using Bing to perform speech to text. Requires internet connection.
 */
public class BingInput implements InputDevice 
{

	@Override
	public Input listen() throws InterruptedException 
	{
		Service BingSTT = new Service(Ros.getInstance(), "TextSpoken", "TextSpoken");
	    ServiceRequest request = new ServiceRequest("{}");
//	    ServiceRequest request = new ServiceRequest("{\"text\": \"eat a dick java!\"}");
	    System.out.println("Sending TTS request");	
	    ServiceResponse response = BingSTT.callServiceAndWait(request);
	    
	    String input = response.toString();
	    
	    JSONObject obj = new JSONObject(input);
	    String text = obj.getString("text");
	    
	    System.out.println(text);
		return new Input(text);
	}
	
}
