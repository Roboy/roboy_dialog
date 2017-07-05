package roboy.dialog.personality.states;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import roboy.linguistics.Linguistics;
import roboy.linguistics.Triple;
import roboy.linguistics.Linguistics.SEMANTIC_ROLE;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.WorkingMemory;
import roboy.util.Lists;

public class PersonalQAState extends AbstractBooleanState{

	private List<String> questions;
	private List<String[]> successTexts;
	private String predicate;
	
	public PersonalQAState(List<String> questions, List<String> failureTexts, 
			List<String[]> successTexts, String predicate) {
		this.questions = questions;
		this.successTexts = successTexts;
		this.predicate = predicate;
		setFailureTexts(failureTexts);
	}
	
	@Override
	public List<Interpretation> act() {
		return Lists.interpretationList(new Interpretation(questions.get((int)Math.random()*questions.size())));
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean determineSuccess(Interpretation input) {
		String[] tokens = (String[]) input.getFeatures().get(Linguistics.TOKENS);
		String answer = null;
		if(tokens.length==1){
			answer = tokens[0];
		} else {
			Map<SEMANTIC_ROLE,Object> pas = (Map<SEMANTIC_ROLE,Object>) input.getFeature(Linguistics.PAS);
			if(pas==null) return false;
			String predicate = ((String)pas.get(SEMANTIC_ROLE.PREDICATE)).toLowerCase();
			String agent = (String)pas.get(SEMANTIC_ROLE.AGENT);
			String patient = (String)pas.get(SEMANTIC_ROLE.PATIENT);
			if(agent==null) return false;
			if(patient==null) return false;
			if(!"am".equals(predicate) && !agent.toLowerCase().contains("i") && !agent.toLowerCase().contains("my")) return false;
			answer = patient;
		}
		if(answer!=null){
			WorkingMemory memory = WorkingMemory.getInstance();
			List<Triple> nameTriple = memory.retrieve(new Triple("is","name",null));
			if(nameTriple.isEmpty()) return false;
			String name = nameTriple.get(0).patiens;
			WorkingMemory.getInstance().save(new Triple(predicate,name,answer));
			List<String> sTexts = new ArrayList<>();
			for(String[] s: successTexts){
				sTexts.add(s[0]+answer+s[1]);
			}
			setSuccessTexts(sTexts);
			return true;
		}
		return false;
	}

}
