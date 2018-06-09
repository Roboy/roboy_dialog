package roboy.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for map related tasks.
 */
public class Maps {

	public static Map<String,String> stringMap(String... elements){
		Map<String, String> result = new HashMap<>();
		for(int i=0; i<elements.length; i+=2){
			result.put(elements[i],elements[i+1]);
		}
		return result;
	}
	
	public static Map<String,Object> stringObjectMap(Object... elements){
		Map<String, Object> result = new HashMap<>();
		for(int i=0; i<elements.length; i+=2){
			result.put((String)elements[i],elements[i+1]);
		}
		return result;
	}
	
	public static Map<Integer,String> intStringMap(Object... elements){
		Map<Integer, String> result = new HashMap<>();
		for(int i=0; i<elements.length; i+=2){
			result.put((Integer)elements[i],(String)elements[i+1]);
		}
		return result;
	}
}
