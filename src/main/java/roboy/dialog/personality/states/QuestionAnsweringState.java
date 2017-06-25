package roboy.dialog.personality.states;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import roboy.linguistics.Linguistics;
import roboy.linguistics.Triple;
import roboy.linguistics.Linguistics.SENTENCE_TYPE;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.logic.PASInterpreter;
import roboy.memory.DBpediaMemory;
import roboy.memory.Memory;
import roboy.util.Lists;
import roboy.util.Relation;

/**
 * State in which Roboy is answering questions based on DBpedia or the knowledge base
 * from the resources folder.
 */
public class QuestionAnsweringState implements State{

	private boolean first = true;
	private List<Triple> memory;
	private State inner;
	private State top;
	private List<Memory<Relation>> memories;
	
	public QuestionAnsweringState(State inner){
		this.inner = inner;
		memories = new ArrayList<>();
		memories.add(DBpediaMemory.getInstance());

	    // fill memory
	    memory = new ArrayList<>(); //TODO: Refactor all that triples stuff to separate memory class
		ClassLoader cl = this.getClass().getClassLoader();
		InputStream inputStream = cl.getResourceAsStream("knowledgebase/triples.csv");
	    try{
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
			String line = br.readLine();
			while(line!=null){
				String[] parts = line.split(",");
				memory.add(new Triple(parts[0], parts[1], parts[2]));
				line = br.readLine();
			}
			br.close();
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	}
	
	public void setTop(State top){
		this.top = top;
	}

	@Override
	public List<Interpretation> act() {
		List<Interpretation> action = first ?
				Lists.interpretationList(new Interpretation("I'm pretty good at answering questions about myself and other stuff. " +
						"What would you like to know?"))
			  : Lists.interpretationList(new Interpretation("Anything else?"));
		first = false;
		return action;
	}

	/**
	 * Checks the sentence type and detected triples in the input for determining what is
	 * asked about. Then checks its knowledge base to come up with an answer.
	 */
	@Override
	public Reaction react(Interpretation input) {
		Triple triple = (Triple) input.getFeatures().get(Linguistics.TRIPLE);
		
		// reverse you <-> I
		if(triple!=null && triple.agens!=null && "you".equals(triple.agens.toLowerCase())) triple.agens = "i";
		if(triple!=null && triple.patiens!=null && "you".equals(triple.patiens.toLowerCase())) triple.patiens = "i";
		if(triple!=null && triple.predicate!=null && "are".equals(triple.predicate.toLowerCase())) triple.predicate = "am";
		
		List<Interpretation> result = new ArrayList<>();
		if(triple!=null && input.getSentenceType() == SENTENCE_TYPE.DOES_IT || input.getSentenceType() == SENTENCE_TYPE.IS_IT){
			List<Triple> t = remember(triple.predicate, triple.agens, null);
			if(t.isEmpty()){
				return innerReaction(input,result);
			} else {
				result.add(new Interpretation("Yes. "));
				for(int i=0; i<t.size(); i++){
					String prefix = (i>0 && i==t.size()-1) ? "also, " : "";
					result.add(new Interpretation(prefix+t.get(i).agens+" "+t.get(i).predicate+" "+t.get(i).patiens));
				}
			}
		} else if(triple!=null && input.getSentenceType() == SENTENCE_TYPE.WHO){
			List<Triple> t = remember(triple.predicate, triple.agens, triple.patiens);
			if(t.isEmpty()){
				return innerReaction(input,result);
			} else {
				for(int i=0; i<t.size(); i++){
					String prefix = (i>0 && i==t.size()-1) ? "also, " : "";
					result.add(new Interpretation(prefix+t.get(i).agens+" "+t.get(i).predicate+" "+t.get(i).patiens));
				}
			}
		} else if(triple!=null && input.getSentenceType() == SENTENCE_TYPE.WHAT){
			List<Triple> t = remember(triple.predicate, triple.agens, triple.patiens);
			if(t.isEmpty()){
				return innerReaction(input,result);
			} else {
				for(int i=0; i<t.size(); i++){
					String prefix = (i>0 && i==t.size()-1) ? "also, " : "";
					result.add(new Interpretation(prefix+t.get(i).agens+" "+t.get(i).predicate+" "+t.get(i).patiens));
				}
			}
		} else if(triple!=null && input.getSentenceType() == SENTENCE_TYPE.HOW_DO){
			List<Triple> t = remember(triple.predicate, triple.agens, null);
			if(t.isEmpty()){
				return innerReaction(input,result);
			} else {
				for(int i=0; i<t.size(); i++){
					String prefix = (i>0 && i==t.size()-1) ? "also, " : "";
					result.add(new Interpretation(prefix+t.get(i).agens+" "+t.get(i).predicate+" "+t.get(i).patiens));
				}
			}
		}
		else {
			return innerReaction(input,result);
		}
		return new Reaction(this, result);
	}

	@SuppressWarnings("unchecked")
	private Reaction innerReaction(Interpretation input,List<Interpretation> result){
		Map<String, Object> pas = (Map<String, Object>) input.getFeature(Linguistics.PAS);
		if(pas!=null && !pas.isEmpty()){
			Relation relation = PASInterpreter.pas2DBpediaRelation(pas);
//			System.out.println("Relation: "+relation);
			for(Memory<Relation> mem : memories){
				try{
					List<Relation> rememberedList = mem.retrieve(relation);
					for (Relation remembered: rememberedList)
					{
						if(remembered!=null&&remembered.object!=null){ // TODO: check for proper role
						result.add(new Interpretation((String)remembered.object.getAttribute(Linguistics.NAME)));
						return new Reaction(this,result);
					}
					}
					
				} catch(Exception e){}
			}
		}
		return inner.react(input);
	}
	
	private List<Triple> remember(String predicate, String agens, String patiens){
		List<Triple> triples = new ArrayList<>();
		for(Triple t: memory){
			if(
					(predicate==null || predicate.toLowerCase().equals(t.predicate)) &&
					(agens==null || agens.toLowerCase().equals(t.agens)) &&
					(patiens==null || patiens.toLowerCase().equals(t.patiens)) 
					){
				triples.add(t);
			}
		}
		return triples;
	}
}
