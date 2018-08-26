package org.roboy.io;

import java.util.List;

import edu.wpi.rail.jrosbridge.Service;
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import org.roboy.dialog.action.Action;
import org.roboy.dialog.action.SpeechAction;
import org.roboy.ros.Ros;

/**
 * Uses Bing for text to speech. Requires internet connection.
 */
public class BingOutput implements OutputDevice {

	@Override
	public void act(List<Action> actions) {
		for(Action a : actions){
			if(a instanceof SpeechAction){
				say(((SpeechAction) a).getText());
			}
		}
	}
	
	public void say(String text)
	{
	    Service BingSTT = new Service(Ros.getInstance(), "TextToSay", "TextToSay");
	    ServiceRequest request = new ServiceRequest("{\"text\": " + "\"" + text + "\"}");
	    BingSTT.callServiceAndWait(request);
	}
	
}
