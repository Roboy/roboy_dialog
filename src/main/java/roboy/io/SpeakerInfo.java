package roboy.io;
/**
 * specific for the dialog between Roboy and more than one speaker
 * every speaker has an id and with every input the current number of speakers is specified 
 */
public class SpeakerInfo {
	private int id;
	private int speakerCount;
	
	public SpeakerInfo(int id, int speakerCount){
		this.id = id;
		this.speakerCount = speakerCount;
	}
	
	public int getID() {
		return this.id;
	}
	
	public int getSpeakerCount(){
		return this.speakerCount;
	}

}
