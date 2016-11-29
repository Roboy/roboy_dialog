package de.roboy.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.roboy.dialog.action.Action;

public class Lists {
	
	public static List<Action> actionList(Action... actions){
		List<Action> result = new ArrayList<>();
		result.addAll(Arrays.asList(actions));
		return result;
	}
	
	public static List<String> stringList(String... strings){
		List<String> result = new ArrayList<>();
		result.addAll(Arrays.asList(strings));
		return result;
	}

}
