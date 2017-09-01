package roboy.dialog.personality.states;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import roboy.linguistics.Linguistics;
import roboy.linguistics.Linguistics.SEMANTIC_ROLE;
import roboy.linguistics.Triple;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.Neo4jMemory;
import roboy.memory.PersistentKnowledge;
import roboy.memory.WorkingMemory;
import roboy.memory.nodes.Interlocutor;
import roboy.memory.nodes.MemoryNodeModel;
import roboy.util.Lists;

/**
 * Roboy introduces himself and asks "Who are you?". Moves to success state if the answer
 * is at most 2 words.
 */
public class IntroductionState extends AbstractBooleanState{

	Interlocutor person = new Interlocutor();

	private static final List<String> introductions = Lists.stringList(
			"I am Roboy. Who are you?",
			"My name is Roboy. What is your name?"
			);
	
	public IntroductionState(Interlocutor person) {
		setFailureTexts(Lists.stringList(
				"It's always nice to meet new people.",
				"How refreshing to see a new face."));
		this.person = person;
	}
	
	@Override
	public List<Interpretation> act() {
		return Lists.interpretationList(new Interpretation(introductions.get((int)Math.random()*introductions.size())));
	}

	/**
	 * Performs person detection by consulting memory.
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected boolean determineSuccess(Interpretation input) {
		String[] tokens = (String[]) input.getFeatures().get(Linguistics.TOKENS);
		String name = null;
		if(tokens.length==1){
			name = tokens[0];
		} else {
			Map<SEMANTIC_ROLE,Object> pas = (Map<SEMANTIC_ROLE,Object>) input.getFeature(Linguistics.PAS);
			if(pas==null || !pas.containsKey(SEMANTIC_ROLE.PREDICATE)) return false;
			String predicate = ((String)pas.get(SEMANTIC_ROLE.PREDICATE)).toLowerCase();
			String agent = (String)pas.get(SEMANTIC_ROLE.AGENT);
			String patient = (String)pas.get(SEMANTIC_ROLE.PATIENT);
			//if(agent==null) agent = "i";
			//TODO Handle cases where name could not be parsed.
			// Maybe something like "I did not quite get your name, could you repeat it."
			// When using a default value with persistent memory, Roboy will always recognize them.
			if(patient==null) agent = "laura";
			//if(!"am".equals(predicate) && !"is".equals(predicate)) return false;
			//if(!agent.toLowerCase().contains("i") && !agent.toLowerCase().contains("my")) return false;
			name = patient;
		}
		if(name!=null){
//			WorkingMemory.getInstance().save(new Triple("is","name",name));
//			List<Triple> agens = PersistentKnowledge.getInstance().retrieve(new Triple(null,name,null));
//			List<Triple> patiens = PersistentKnowledge.getInstance().retrieve(new Triple(null,null,name));
			//TODO Currently assuming no duplicate names in memory. Support for last name addition needed.
			person.addName(name);
			if(!person.FAMILIAR) {
				return false;
			}
			setSuccessTexts(Lists.stringList(
					"Oh hi, "+name+". Sorry, I didn't recognize you at first. But you know how the vision guys are.",
					"Hi "+name+" nice to see you again."
					));
			return true;
		}
		return false;
	}

}
