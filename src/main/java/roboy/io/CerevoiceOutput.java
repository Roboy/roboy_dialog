package roboy.io;

import java.util.List;
import roboy.dialog.action.Action;
import roboy.dialog.action.SpeechAction;

import roboy.util.RosMainNode;

/**
 * Cerevoice text to speech
 */
public class CerevoiceOutput implements OutputDevice
{

	@Override
	public void act(List<Action> actions) {
		for(Action a : actions){
			if(a instanceof SpeechAction){
				final String textToSay = ((SpeechAction) a).getText();
				say(textToSay);
			}
		}
	}
	
	public void say(String text)
	{
		RosMainNode.getInstance().SynthesizeSpeech(text);
	}

}
