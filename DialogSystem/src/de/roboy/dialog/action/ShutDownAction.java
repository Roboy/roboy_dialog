package de.roboy.dialog.action;

import java.util.List;

public class ShutDownAction implements Action{

	private List<Action> lastwords;
	
	public ShutDownAction(List<Action> lastwords){
		this.lastwords = lastwords;
	}
	
	public List<Action> getLastWords(){
		return lastwords;
	}

}
