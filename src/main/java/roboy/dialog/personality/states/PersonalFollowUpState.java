package roboy.dialog.personality.states;

import com.google.gson.Gson;
import roboy.linguistics.Linguistics;
import roboy.linguistics.Linguistics.SEMANTIC_ROLE;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.Neo4jMemory;
import roboy.memory.Neo4jMemoryInterface;
import roboy.memory.Neo4jRelationships;
import roboy.memory.nodes.Interlocutor;
import roboy.memory.nodes.MemoryNodeModel;
import roboy.util.Lists;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PersonalFollowUpState extends AbstractBooleanState{

	private List<String> questions;
	private List<String> successTexts;
	public Neo4jRelationships predicate;
	private Interlocutor person;
	private Neo4jMemoryInterface memory;

	public PersonalFollowUpState(Neo4jMemoryInterface memory,
								 List<String> questions, List<String> failureTexts,
								 List<String> successTexts, Neo4jRelationships predicate,
								 QuestionRandomizerState nextState, Interlocutor person) {
		this.memory = memory;
		this.questions = questions;
		this.successTexts = successTexts;
		this.predicate = predicate;
		this.person = person;
		this.setNextState(nextState);
		setFailureTexts(failureTexts);
	}

	/**
	 * Ask the question.
     * Using Neo4jRelationships predicate
	 */
	@Override
	public List<Interpretation> act() {
		String retrievedResult = "";
        ArrayList<Integer> ids = person.getRelationships(predicate);
        try {
            MemoryNodeModel requestedObject = new MemoryNodeModel(memory);
            requestedObject.fromJSON(memory.getById(ids.get(0)), new Gson());
            retrievedResult = requestedObject.getProperties().get("name").toString();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Lists.interpretationList(new Interpretation(String.format(questions.get((int)Math.random()*questions.size()), retrievedResult)));
	}

	/**
	 * Retrieve the answer and add it to the memory, if needed.
	 *
	 * As locations, hobbies, workplaces etc are individual nodes in memory,
	 * those will be retrieved or created if necessary.
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected boolean determineSuccess(Interpretation input) {
		String[] tokens = (String[]) input.getFeatures().get(Linguistics.TOKENS);
		String answer = null;
		if(tokens.length==1){
			answer = tokens[0];
		}
		else if(input.getFeatures().containsKey(Linguistics.OBJ_ANSWER))
		{
			answer = (String) input.getFeature(Linguistics.OBJ_ANSWER);
		}
		else {
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

			// TODO Remove old code after successfully switching to Neo4j memory
			//WorkingMemory memory = WorkingMemory.getInstance();
			// List<Triple> nameTriple = memory.retrieve(new Triple("is","name",null));
			//if(nameTriple.isEmpty()) return false;
			//String name = nameTriple.get(0).object;
			//WorkingMemory.getInstance().save(new Triple(predicate,name,answer));

			// Add the new information about the person to the memory.
			// person.addInformation(predicate.type, answer);


			List<String> sTexts = new ArrayList<>();
			for(String s: successTexts){
				sTexts.add(String.format(s, ""));
			}
			setSuccessTexts(sTexts);
			return true;
		}
		return false;
	}

}
