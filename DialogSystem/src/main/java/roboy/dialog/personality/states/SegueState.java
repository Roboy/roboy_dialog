package roboy.dialog.personality.states;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roboy.linguistics.DetectedEntity;
import roboy.linguistics.Entity;
import roboy.linguistics.Linguistics;
import roboy.linguistics.Linguistics.SENTENCE_TYPE;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.util.Lists;
import roboy.util.Maps;

public class SegueState implements State{
	
	private static final Map<SENTENCE_TYPE, Reaction> sentenceTypeAssociations = new HashMap<>();
	
	private State inner;
//	private List<State> possibilities;
	private Map<String,String> redditTIL;
	
	public SegueState(State inner){ // , List<State> possibilities
		this.inner = inner;
//		this.possibilities = possibilities;
		redditTIL = new HashMap<>();
		ClassLoader cl = this.getClass().getClassLoader();
		URL url = cl.getResource("knowledgebase/triviaWords.csv");
	    File trivia=null;
		try {
			trivia = new File(url.toURI());
			BufferedReader br = new BufferedReader(new FileReader(trivia));
			String line;
			while((line=br.readLine())!=null){
				String[] parts = line.split(";");
				redditTIL.put(parts[0],parts[2]);
			}
			br.close();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Interpretation> act() {
		return inner.act();
	}

	@Override
	public Reaction react(Interpretation input) {
		Reaction reaction = inner.react(input);
		return segway(input,reaction);
	}
	
	@SuppressWarnings("unchecked")
	private Reaction segway(Interpretation input, Reaction defaultReaction){
		// sentence type associations
		SENTENCE_TYPE sentenceType = input.getSentenceType();
		
		// relation associations
		
		// word associations
		if(input.getFeatures().containsKey(Linguistics.KEYWORDS)){
			input.getFeatures().get(Linguistics.KEYWORDS);
			List<DetectedEntity> detectedEntities = 
					(List<DetectedEntity>)input.getFeatures().get(Linguistics.KEYWORDS);
			for(DetectedEntity e : detectedEntities){
				String base = e.getEntity().getForm("base");
				if(redditTIL.containsKey(base)){
					String anecdote = redditTIL.get(base);
					return new Reaction(new AnecdoteState(this, anecdote),
							Lists.interpretationList(new Interpretation(SENTENCE_TYPE.SEGUE, 
									Maps.stringObjectMap(Linguistics.ASSOCIATION,base))));
				}
			}
		}
		
		// else
		return defaultReaction;
	}
	
}
