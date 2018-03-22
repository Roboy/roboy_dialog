package roboy.dialog.personality;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import roboy.dialog.action.Action;
import roboy.dialog.action.SpeechAction;
import roboy.linguistics.Linguistics;
import roboy.linguistics.Linguistics.SENTENCE_TYPE;
import roboy.linguistics.Triple;
import roboy.linguistics.sentenceanalysis.Interpretation;

@Deprecated
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
	public List<Action> answer(Interpretation sentence) {

		Triple triple = (Triple) sentence.getFeatures().get(Linguistics.TRIPLE);
		List<Action> result = new ArrayList<>();
		if(sentence.getSentenceType() == SENTENCE_TYPE.DOES_IT || sentence.getSentenceType() == SENTENCE_TYPE.IS_IT){
			Triple t = remember(triple.predicate, triple.subject, triple.object);
			if(t==null){
				result.add(new SpeechAction("No, not that I know of."));
			} else {
				result.add(new SpeechAction("Yes."));
			}
		} else if(sentence.getSentenceType() == SENTENCE_TYPE.HOW_IS){
			Triple t = remember(triple.predicate, triple.subject, triple.object);
			if(t==null){
				result.add(new SpeechAction("I don't know. You tell me."));
			} else {
				result.add(new SpeechAction(t.object));
			}
		} else if(sentence.getSentenceType() == SENTENCE_TYPE.WHO){
			Triple t = remember(triple.predicate, triple.subject, triple.object);
			if(t==null){
				result.add(new SpeechAction("I don't know. You tell me."));
			} else {
				result.add(new SpeechAction(t.subject));
			}
		} else if(sentence.getSentenceType() == SENTENCE_TYPE.STATEMENT && triple.predicate!=null){
			result.add(new SpeechAction("Great, I will keep that in mind."));
			memory.add(new Triple(triple.predicate.toLowerCase(), triple.subject.toLowerCase(), triple.object.toLowerCase()));
		} else {
			result.add(new SpeechAction("Ok, if you say so."));
		}
		return result;
	}
	
	private Triple remember(String predicate, String agens, String patiens){
		for(Triple t: memory){
			if(
					(predicate==null || predicate.toLowerCase().equals(t.predicate)) &&
					(agens==null || agens.toLowerCase().equals(t.subject)) &&
					(patiens==null || patiens.toLowerCase().equals(t.object))
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
