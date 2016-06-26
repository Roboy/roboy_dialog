package de.roboy.dialog.action;

public class SpeechAction implements Action{

	private String text;
	
	public SpeechAction(String text){
		this.text = text;
	}
	
	public String getText(){
		return text;
	}
}
