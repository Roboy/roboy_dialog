package roboy.dialog.action;

import java.util.List;

/**
 * Action used to shut down Roboy. Sending a ShutDownAction leads the dialog manager
 * to leave the communication loop in the DialogManager class and quit the program
 * after uttering the given last words.
 */
public class ShutDownAction implements Action{

	private List<Action> lastwords;
	
	/**
	 * Constructor.
	 * 
	 * @param lastwords The last actions that Roboy should perform before shutting down
	 */
	public ShutDownAction(List<Action> lastwords){
		this.lastwords = lastwords;
	}
	
	public List<Action> getLastWords(){
		return lastwords;
	}

}
