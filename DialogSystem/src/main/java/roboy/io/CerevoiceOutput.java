package roboy.io;

import java.util.List;

import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Service;
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import roboy.dialog.action.Action;
import roboy.dialog.action.SpeechAction;

public class CerevoiceOutput implements OutputDevice 
{
	private Ros ros;
	
	public CerevoiceOutput()
	{
		ros = new Ros("localhost");
	    ros.connect();
	    System.out.println("ROS bridge is set up");	
	}
	
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
	    Service CerevoiceTTS = new Service(ros, "CerevoiceTTS", "CerevoiceTTS");
	    ServiceRequest request = new ServiceRequest("{\"text\": " + "\"" + text + "\"}");
	    CerevoiceTTS.callServiceAndWait(request);
	}
	
}
