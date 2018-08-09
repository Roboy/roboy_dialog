package roboy.io;

import java.util.List;

import roboy.dialog.action.Action;
import roboy.dialog.action.EmotionAction;
import roboy.dialog.action.SpeechAction;

/**
 * Uses the command line as output device.
 */
public class CommandLineOutput implements OutputDevice{

	private boolean emotionFlag = false; //turn on/off emotion output

//	private EmotionOutput emotion;
//	public CommandLineOutput(EmotionOutput emotion)
//	{
//		this.emotion = emotion;
//	}
	@Override
	public void act(List<Action> actions) {
		for(Action a : actions){
			if(a instanceof SpeechAction){
//				int duration = ((SpeechAction) a).getText().length()==0 ? 0 : 1 + ((SpeechAction) a).getText().length()/8;
//				emotion.act(new EmotionAction("speak", duration));
				System.out.println("[Roboy]: " + ((SpeechAction) a).getText());
			}else if (emotionFlag == true && a instanceof EmotionAction) {
				System.out.println("[RoboyEmotion]: " + ((EmotionAction) a).getState());
			}
		}
	}
}
