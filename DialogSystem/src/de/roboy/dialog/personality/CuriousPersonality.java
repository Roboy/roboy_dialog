package de.roboy.dialog.personality;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import de.roboy.dialog.action.Action;
import de.roboy.dialog.action.SpeechAction;
import de.roboy.linguistics.Term;
import de.roboy.linguistics.Triple;

public class CuriousPersonality implements Personality {
	
	public enum SENTENCE_TYPE { WHO, HOW, WHY, WHEN, WHERE, WHAT, DOES_IT, STATEMENT, NONE}
	
	private Map<String,String> meanings;
	private List<Triple> memory;
	
	
	public CuriousPersonality() throws JsonSyntaxException, JsonIOException, IOException {
		// find meaning
		meanings = new HashMap<>();
		ClassLoader cl = this.getClass().getClassLoader();
		URL url = cl.getResource("ontology/terms");
		Gson gson = new Gson();
	    File dir=null;
		try {
			dir = new File(url.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	    for (File nextFile : dir.listFiles()) {
	    	Term t = gson.fromJson(new FileReader(nextFile), Term.class);
	    	meanings.put(nextFile.getName().substring(0,nextFile.getName().length()-5), t.concept);
	    }
	    // fill memory
	    memory = new ArrayList<>();
	    File f = new File(cl.getResource("knowledgebase/triples.csv").getFile());
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line = br.readLine();
		while(line!=null){
			String[] parts = line.split(",");
			memory.add(new Triple(parts[0], parts[1], parts[2]));
			line = br.readLine();
		}
		br.close();
	}

	@Override
	public List<Action> answer(String input) {
		String[] tokens = input.split("\\s+");
		SENTENCE_TYPE type = determineSentenceType(tokens);
		String predicate = null;
		String agens = "";
		String patiens = "";
		if(type == SENTENCE_TYPE.STATEMENT){
			for(int i=0; i<tokens.length; i++){
				if(meanings.containsKey(tokens[i])){
					for(int j=0; j<i; j++){
						if(j>0) agens+=" ";
						agens+=tokens[j];
					}
					predicate = tokens[i]; //meanings.get(tokens[i]);
					for(int j=i+1; j<tokens.length; j++){
						if(j>i+1) patiens+=" ";
						patiens+=tokens[j];
					}
				}
			}
		} else if(type == SENTENCE_TYPE.DOES_IT){
			if(tokens.length>2 && meanings.containsKey(tokens[0].toLowerCase())){
				predicate = tokens[0];
				agens = tokens[1];
				for(int j=2; j<tokens.length; j++){
					if(j>2) patiens+=" ";
					patiens+=tokens[j];
				}
			}
		} else if(type == SENTENCE_TYPE.WHO){
			if(tokens.length>2 && meanings.containsKey(tokens[1].toLowerCase())){
				agens = null;
				predicate = tokens[1];
				for(int j=2; j<tokens.length; j++){
					if(j>2) patiens+=" ";
					patiens+=tokens[j];
				}
			}
		} else if(type == SENTENCE_TYPE.HOW){
			if(tokens.length>2 && meanings.containsKey(tokens[1].toLowerCase())){
				patiens = null;
				predicate = tokens[1];
				for(int j=2; j<tokens.length; j++){
					if(j>2) agens+=" ";
					agens+=tokens[j];
				}
			}
		}
		List<Action> result = new ArrayList<>();
		if(type == SENTENCE_TYPE.DOES_IT){
			Triple t = remember(predicate, agens, patiens);
			if(t==null){
				result.add(new SpeechAction("No, not that I know of."));
			} else {
				result.add(new SpeechAction("Yes."));
			}
		} else if(type == SENTENCE_TYPE.HOW){
			Triple t = remember(predicate, agens, patiens);
			if(t==null){
				result.add(new SpeechAction("I don't know. You tell me."));
			} else {
				result.add(new SpeechAction(t.patiens));
			}
		} else if(type == SENTENCE_TYPE.WHO){
			Triple t = remember(predicate, agens, patiens);
			if(t==null){
				result.add(new SpeechAction("I don't know. You tell me."));
			} else {
				result.add(new SpeechAction(t.agens));
			}
		} else if(type == SENTENCE_TYPE.STATEMENT && predicate!=null){
			result.add(new SpeechAction("Great, I will keep that in mind."));
			memory.add(new Triple(predicate.toLowerCase(), agens.toLowerCase(), patiens.toLowerCase()));
		} else {
			result.add(new SpeechAction("Ok, if you say so."));
		}
		return result;
	}
	
	private Triple remember(String predicate, String agens, String patiens){
		for(Triple t: memory){
			if(
					(predicate==null || predicate.toLowerCase().equals(t.predicate)) &&
					(agens==null || agens.toLowerCase().equals(t.agens)) &&
					(patiens==null || patiens.toLowerCase().equals(t.patiens)) 
					){
				return t;
			}
		}
		return null;
	}
	
	private SENTENCE_TYPE determineSentenceType(String[] tokens){
		if(tokens.length==0) return SENTENCE_TYPE.NONE;
		String first = tokens[0].toLowerCase();
		if("who".equals(first)) return SENTENCE_TYPE.WHO;
		if("where".equals(first)) return SENTENCE_TYPE.WHERE;
		if("what".equals(first)) return SENTENCE_TYPE.WHAT;
		if("when".equals(first)) return SENTENCE_TYPE.WHEN;
		if("why".equals(first)) return SENTENCE_TYPE.WHY;
		if("how".equals(first)) return SENTENCE_TYPE.HOW;
		if("do".equals(first)) return SENTENCE_TYPE.DOES_IT;
		if("does".equals(first)) return SENTENCE_TYPE.DOES_IT;
		if("did".equals(first)) return SENTENCE_TYPE.DOES_IT;
		if("is".equals(first)) return SENTENCE_TYPE.DOES_IT;
		if("are".equals(first)) return SENTENCE_TYPE.DOES_IT;
		if("am".equals(first)) return SENTENCE_TYPE.DOES_IT;
		return SENTENCE_TYPE.STATEMENT;
	}
	
	public static void main(String[] args) throws Exception{
		new CuriousPersonality();
	}

}
