package roboy.logic;

import java.util.List;

public class StatementInterpreter {

	/**
	 * Checks if the given String contains one of the Strings from the given list.
	 * 
	 * @param input
	 * @param list
	 * @return
	 */
	public static boolean isFromList(String input, List<String> list){
		for(String l: list){
			if(input.toLowerCase().contains(l)) return true;
		}
		return false;
	}
}
