package roboy.dialog.personality.states;

import java.util.List;
import java.util.Map;

import roboy.linguistics.Linguistics;
import roboy.linguistics.Linguistics.SEMANTIC_ROLE;
import roboy.linguistics.Triple;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.WorkingMemory;
import roboy.util.Lists;

/**
 * Roboy introduces himself and asks "How are you?". Moves to success state if the answer
 * is at most 2 words.
 */
public class IntroductionState extends AbstractBooleanState{

	private static final List<String> introductions = Lists.stringList(
			"I am Roboy. Who are you?",
			"Nice to meet you. My name is Roboy. What is your name?"
			);
	
	@Override
	public List<Interpretation> act() {
		return Lists.interpretationList(new Interpretation(introductions.get((int)Math.random()*introductions.size())));
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean determineSuccess(Interpretation input) {
		String[] tokens = (String[]) input.getFeatures().get(Linguistics.TOKENS);
		String name = null;
		if(tokens.length==1){
			name = tokens[0];
		} else {
			Map<SEMANTIC_ROLE,Object> pas = (Map<SEMANTIC_ROLE,Object>) input.getFeature(Linguistics.PAS);
			if(pas==null) return false;
			String predicate = ((String)pas.get(SEMANTIC_ROLE.PREDICATE)).toLowerCase();
			String agent = (String)pas.get(SEMANTIC_ROLE.AGENT);
			String patient = (String)pas.get(SEMANTIC_ROLE.PATIENT);
			if(agent==null) return false;
			if(patient==null) return false;
			if(!"am".equals(predicate) && !"is".equals(predicate)) return false;
			if(!agent.toLowerCase().contains("i") && !agent.toLowerCase().contains("my")) return false;
			name = patient;
		}
		if(name!=null){
			WorkingMemory.getInstance().save(new Triple("is","name",name));
			return true;
		}
		return false;
	}

}
