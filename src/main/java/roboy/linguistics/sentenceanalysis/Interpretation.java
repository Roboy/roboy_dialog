package roboy.linguistics.sentenceanalysis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roboy.linguistics.Linguistics;
import roboy.linguistics.Linguistics.SENTENCE_TYPE;
import roboy.linguistics.Triple;

/**
 * An interpretation of all inputs to Roboy consists of the sentence type and an
 * arbitrary map of features. Feature names are listed and documented in the 
 * class roboy.linguistics.Linguistics.
 * 
 * The interpretation class is also used to pass the output information from the
 * states to the verbalizer class.
 */
public class Interpretation {

	private Map<String,Object> features;
	private SENTENCE_TYPE sentenceType; //TODO: turn this into a feature


	// new type safe fields to use with semantic parser, refactor to private fields + getter & setter later
	// getting Objects without any type information from the hash map is not a good practice, this is not JavaScript
	public String answer;
	public String underspecifiedQuestion;
	public List<Triple> semParserTriples;
	public Linguistics.PARSER_OUTCOME parserOutcome;



	
	public Interpretation(String sentence){
		features = new HashMap<>();
		features.put(Linguistics.SENTENCE,sentence);
		sentenceType = SENTENCE_TYPE.STATEMENT;
	}
	
	public Interpretation(String sentence, Map<String,Object> features){
		this.features = features;
		this.features.put(Linguistics.SENTENCE,sentence);
		sentenceType = SENTENCE_TYPE.STATEMENT;
	}
	
	public Interpretation(SENTENCE_TYPE sentenceType){
		this.sentenceType = sentenceType;
		features = new HashMap<>();
	}
	
	public Interpretation(SENTENCE_TYPE sentenceType, Map<String,Object> features){
		this.sentenceType = sentenceType;
		this.features = features;
	}

	public Map<String, Object> getFeatures() {
		return features;
	}
	
	public Object getFeature(String featureName){
		return features.get(featureName);
	}

	public void setFeatures(Map<String, Object> features) {
		this.features = features;
	}

	public SENTENCE_TYPE getSentenceType() {
		return sentenceType;
	}

	public void setSentenceType(SENTENCE_TYPE sentenceType) {
		this.sentenceType = sentenceType;
	}

	@Override
	public String toString(){
		return sentenceType+" "+features;
	}
}
