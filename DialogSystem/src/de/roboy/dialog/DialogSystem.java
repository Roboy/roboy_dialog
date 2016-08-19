package de.roboy.dialog;

import java.io.IOException;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import de.roboy.dialog.personality.CuriousPersonality;
import de.roboy.dialog.personality.DefaultPersonality;
import de.roboy.dialog.personality.Personality;
import de.roboy.io.CommandLineCommunication;
import de.roboy.io.Communication;

public class DialogSystem {
	
	public static void main(String[] args) throws JsonSyntaxException, JsonIOException, IOException {
//		Personality p = new DefaultPersonality();
		Personality p = new CuriousPersonality();
		Communication c = new CommandLineCommunication();
		c.setPersonality(p);
		c.communicate();
	}

}
