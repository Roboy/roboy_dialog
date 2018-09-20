package roboy.dialog.action;

/**
 * Action used for replaying a sound file.
 */
public class SoundAction implements Action{

	private String filename;

	/**
	 * Constructor.
	 *
	 * @param filename absolute path of the sound file
	 */
	public SoundAction(String filename){
		this.filename = filename;
	}
	
	public String getFilename(){
		return filename;
	}
}
