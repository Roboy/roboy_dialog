package de.roboy.dialog;

import de.roboy.dialog.personality.DefaultPersonality;
import de.roboy.dialog.personality.Personality;
import de.roboy.io.CommandLineCommunication;
import de.roboy.io.Communication;

public class DialogSystem {
	
	public static void main(String[] args) {
		Personality p = new DefaultPersonality();
		Communication c = new CommandLineCommunication();
		c.setPersonality(p);
		c.communicate();
	}

}
