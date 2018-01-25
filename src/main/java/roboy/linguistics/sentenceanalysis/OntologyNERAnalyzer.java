package roboy.linguistics.sentenceanalysis;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roboy.linguistics.DetectedEntity;
import roboy.linguistics.Entity;
import roboy.linguistics.Linguistics;

/**
 * Checks for keywords from a list (knowledgebase/triviaWords.csv) and stores them in 
 * Linguistics.KEYWORDS attribute of the interpretation.
 */
public class OntologyNERAnalyzer implements Analyzer{
	
	private Map<String,Entity> entities;
	
	public OntologyNERAnalyzer() {
		entities = new HashMap<>();
		ClassLoader cl = this.getClass().getClassLoader();
		InputStream inputStream = cl.getResourceAsStream("knowledgebase/triviaWords.csv");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			while((line=br.readLine())!=null){
				String[] parts = line.split(";");
				entities.put(parts[0],new Entity(parts[0]));
			}
			br.close();



		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public Interpretation analyze(Interpretation interpretation) {
		String[] tokens = (String[]) interpretation.getFeatures().get(Linguistics.TOKENS);
		for(int i=0; i<tokens.length; i++){
			if(entities.containsKey(tokens[i].toLowerCase())){
				Entity e = entities.get(tokens[i].toLowerCase());
				DetectedEntity d = new DetectedEntity(e,i);
				if(interpretation.getFeatures().containsKey(Linguistics.KEYWORDS)){
					List<DetectedEntity> detectedEntities = (List<DetectedEntity>)interpretation.getFeatures().get(Linguistics.KEYWORDS);
					detectedEntities.add(d);
				} else {
					List<DetectedEntity> detectedEntities = new ArrayList<>();
					detectedEntities.add(d);
					interpretation.getFeatures().put(Linguistics.KEYWORDS, detectedEntities);
				}
			}
		}
		return interpretation;
	}

}
