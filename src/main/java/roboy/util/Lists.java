package roboy.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import roboy.dialog.action.Action;
import roboy.linguistics.sentenceanalysis.Interpretation;

/**
 * Helper class for list related tasks.
 */
public class Lists {
	
	public static List<Action> actionList(Action... actions){
		List<Action> result = new ArrayList<>();
		result.addAll(Arrays.asList(actions));
		return result;
	}
	
	public static List<Interpretation> interpretationList(Interpretation... interpretations){
		List<Interpretation> result = new ArrayList<>();
		result.addAll(Arrays.asList(interpretations));
		return result;
	}
	
	public static List<String> stringList(String... strings){
		List<String> result = new ArrayList<>();
		result.addAll(Arrays.asList(strings));
		return result;
	}

}
