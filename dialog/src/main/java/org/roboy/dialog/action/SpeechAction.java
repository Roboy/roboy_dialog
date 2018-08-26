package org.roboy.dialog.action;

/**
 * Action used for talking.
 */
public class SpeechAction implements Action{

	private String text;
	
	/**
	 * Constructor.
	 * 
	 * @param text The text RoboyModel will utter
	 */
	public SpeechAction(String text){
		this.text = text;
	}
	
	public String getText(){
		return text;
	}
}
