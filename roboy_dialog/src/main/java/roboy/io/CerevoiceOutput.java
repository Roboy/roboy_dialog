package roboy.io;

import java.util.List;
import roboy.dialog.action.Action;
import roboy.dialog.action.SpeechAction;

import roboy.ros.RosMainNode;

/**
 * Cerevoice text to speech
 */
public class CerevoiceOutput implements OutputDevice
{

	private RosMainNode rosMainNode;

	public CerevoiceOutput (RosMainNode node){
		this.rosMainNode = node;
	}

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
		rosMainNode.SynthesizeSpeech(text);
	}

}
