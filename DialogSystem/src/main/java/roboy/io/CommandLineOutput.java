package roboy.io;

import java.util.List;

import roboy.dialog.action.Action;
import roboy.dialog.action.SpeechAction;

public class CommandLineOutput implements OutputDevice{

	@Override
	public void act(List<Action> actions) {
		for(Action a : actions){
			if(a instanceof SpeechAction){
				System.out.println(((SpeechAction) a).getText());
			}
		}
	}

}
