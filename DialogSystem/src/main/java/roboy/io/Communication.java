package roboy.io;

import roboy.dialog.personality.Personality;

@Deprecated
public interface Communication {

	public void setPersonality(Personality p);
	
	public void communicate();
}
