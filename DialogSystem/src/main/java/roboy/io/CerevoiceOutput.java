package roboy.io;

import java.util.List;

import edu.wpi.rail.jrosbridge.Service;
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import roboy.dialog.action.Action;
import roboy.dialog.action.FaceAction;
import roboy.dialog.action.SpeechAction;
import roboy.util.Ros;

public class CerevoiceOutput implements OutputDevice 
{
	private EmotionOutput emotion;
	public CerevoiceOutput(EmotionOutput emotion)
	{
		this.emotion = emotion;
	}
	@Override
	public void act(List<Action> actions) {
		for(Action a : actions){
			if(a instanceof SpeechAction){
				final String textToSay = ((SpeechAction) a).getText();
				final int duration = ((SpeechAction) a).getText().length()==0 ? 0 : 1 + ((SpeechAction) a).getText().length()/8;
				Runnable r = new Runnable() {
					@Override
					public void run() {
						emotion.act(new FaceAction("speak", duration));
					}
				};

				Thread t = new Thread(r);
				t.start();
				say(textToSay);
//				emotion.act(new FaceAction("neutral"));
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
