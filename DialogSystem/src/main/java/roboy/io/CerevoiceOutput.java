package roboy.io;

import java.util.List;

import edu.wpi.rail.jrosbridge.Service;
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import roboy.dialog.action.Action;
import roboy.dialog.action.SpeechAction;
import roboy.util.Ros;

public class CerevoiceOutput implements OutputDevice 
{
	
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
	    Service CerevoiceTTS = new Service(Ros.getInstance(), "/speech_synthesis/talk", "/speech_synthesis/Talk");
	    ServiceRequest request = new ServiceRequest("{\"text\": " + "\"" + text + "\"}");
	    CerevoiceTTS.callServiceAndWait(request);
	}
	
}
