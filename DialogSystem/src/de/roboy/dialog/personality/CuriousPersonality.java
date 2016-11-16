package de.roboy.dialog.personality;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import de.roboy.dialog.action.Action;
import de.roboy.dialog.action.SpeechAction;
import de.roboy.linguistics.Triple;
import de.roboy.linguistics.sentenceanalysis.Sentence;
import de.roboy.linguistics.sentenceanalysis.Sentence.SENTENCE_TYPE;
import de.roboy.linguistics.sentenceanalysis.SentenceAnalyzer;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

public class CuriousPersonality implements Personality {
	
	private List<Triple> memory;
	
	public CuriousPersonality() throws JsonSyntaxException, JsonIOException, IOException {
	    // fill memory
	    memory = new ArrayList<>();
		ClassLoader cl = this.getClass().getClassLoader();
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
	public List<Action> answer(Sentence sentence) {

		List<Action> result = new ArrayList<>();
		if(sentence.sentenceType == SENTENCE_TYPE.DOES_IT || sentence.sentenceType == SENTENCE_TYPE.IS_IT){
			Triple t = remember(sentence.triple.predicate, sentence.triple.agens, sentence.triple.patiens);
			if(t==null){
				result.add(new SpeechAction("No, not that I know of."));
			} else {
				result.add(new SpeechAction("Yes."));
			}
		} else if(sentence.sentenceType == SENTENCE_TYPE.HOW_IS){
			Triple t = remember(sentence.triple.predicate, sentence.triple.agens, sentence.triple.patiens);
			if(t==null){
				result.add(new SpeechAction("I don't know. You tell me."));
			} else {
				result.add(new SpeechAction(t.patiens));
			}
		} else if(sentence.sentenceType == SENTENCE_TYPE.WHO){
			Triple t = remember(sentence.triple.predicate, sentence.triple.agens, sentence.triple.patiens);
			if(t==null){
				result.add(new SpeechAction("I don't know. You tell me."));
			} else {
				result.add(new SpeechAction(t.agens));
			}
		} else if(sentence.sentenceType == SENTENCE_TYPE.STATEMENT && sentence.triple.predicate!=null){
			result.add(new SpeechAction("Great, I will keep that in mind."));
			memory.add(new Triple(sentence.triple.predicate.toLowerCase(), sentence.triple.agens.toLowerCase(), sentence.triple.patiens.toLowerCase()));
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
	
	
	public static void main(String[] args) throws Exception{
		new CuriousPersonality();
	}

}
