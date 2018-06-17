package roboy.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Helper class for list related tasks.
 */
public class Lists {
	
	public static List<String> stringList(String... strings){
		List<String> result = new ArrayList<>();
		result.addAll(Arrays.asList(strings));
		return result;
	}
}
