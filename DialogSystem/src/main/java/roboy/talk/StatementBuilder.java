package roboy.talk;

import java.util.List;

public class StatementBuilder {

	/**
	 * Returns a random element from the given list of Strings.
	 * 
	 * @param list
	 * @return
	 */
	public static String random(List<String> list){
		return list.get((int)(Math.random()*list.size()));
	}
}
