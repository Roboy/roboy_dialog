package roboy.logic;

import java.util.List;

public class StatementInterpreter {

	public static boolean isFromList(String input, List<String> list){
		for(String l: list){
			if(input.toLowerCase().contains(l)) return true;
		}
		return false;
	}
}
