package roboy.io;

import edu.wpi.rail.jrosbridge.services.std.Empty;
import roboy.util.Ros;
import edu.wpi.rail.jrosbridge.Service;
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import edu.wpi.rail.jrosbridge.services.ServiceResponse;
import org.json.*;
import roboy.util.RosMainNode;

/**
 * Using Bing to perform speech to text. Requires internet connection.
 */
public class BingInput implements InputDevice 
{

	@Override
	public Input listen() throws InterruptedException 
	{
		String text = RosMainNode.getInstance().RecognizeSpeech();
	    System.out.println(text);
		return new Input(text);
	}
	
}
