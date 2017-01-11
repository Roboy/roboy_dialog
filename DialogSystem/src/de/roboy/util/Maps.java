package de.roboy.util;

import java.util.HashMap;
import java.util.Map;

import de.roboy.dialog.personality.states.Reaction;

public class Maps {

	public static Map<String,Object> stringObjectMap(Object... elements){
		Map<String, Object> result = new HashMap<>();
		for(int i=0; i<elements.length; i+=2){
			result.put((String)elements[i],elements[i+1]);
		}
		return result;
	}
	
	public static Map<String,Reaction> stringReactionMap(Object... elements){
		Map<String, Reaction> result = new HashMap<>();
		for(int i=0; i<elements.length; i+=2){
			result.put((String)elements[i],(Reaction)elements[i+1]);
		}
		return result;
	}
}
