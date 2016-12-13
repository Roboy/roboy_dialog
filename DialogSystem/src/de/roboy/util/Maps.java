package de.roboy.util;

import java.util.HashMap;
import java.util.Map;

public class Maps {

	public static Map<String,Object> stringObjectMap(Object... elements){
		Map<String, Object> result = new HashMap<>();
		for(int i=0; i<elements.length; i+=2){
			result.put((String)elements[i],elements[i+1]);
		}
		return result;
	}
}
