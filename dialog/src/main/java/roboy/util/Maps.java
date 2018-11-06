package roboy.util;

import com.github.jsonldjava.utils.Obj;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

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

	//The day when Java finally supports Bidirectional Maps...
	public static <keys, values> Collection<keys> value2Keys(HashMap<keys, values> map, values value){
		ArrayList<keys> collection = new ArrayList<>();
		for(keys k : map.keySet()){
			if(map.get(k).equals(value)){
				collection.add(k);
			}
		}
		return collection;
	}
	public static <keys, values> Optional<keys> value2Key(HashMap<keys, values> map, values value){
		Collection<keys> v2k = value2Keys(map, value);
		if(v2k==null || v2k.isEmpty() || v2k.size()>=2){
			return Optional.empty();
		}
		else return v2k.stream().findFirst();
	}
}
