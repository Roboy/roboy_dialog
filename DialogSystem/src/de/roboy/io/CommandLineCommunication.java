package de.roboy.io;

import java.util.List;
import java.util.Scanner;

import de.roboy.dialog.action.Action;
import de.roboy.dialog.action.ShutDownAction;
import de.roboy.dialog.action.SpeechAction;
import de.roboy.dialog.personality.Personality;

public class CommandLineCommunication implements Communication{

	private Personality personality;
	
	@Override
	public void setPersonality(Personality p) {
		personality = p;
	}

	@Override
	public void communicate() {
		Scanner sc = new Scanner(System.in);
		String input = sc.nextLine();
		List<Action> roboy =  personality.answer(input);
		while(roboy.size()==1 && !(roboy.get(0) instanceof ShutDownAction)){
			talk(roboy);
			input = sc.nextLine();
			roboy = personality.answer(input);
		}
		List<Action> lastwords = ((ShutDownAction)roboy.get(0)).getLastWords();
		talk(lastwords);
	    sc.close();
	}
	
	private void talk (List<Action> actions) {
		for(Action a : actions){
			if(a instanceof SpeechAction){
				System.out.println(((SpeechAction) a).getText());
			}
		}
	}

}
