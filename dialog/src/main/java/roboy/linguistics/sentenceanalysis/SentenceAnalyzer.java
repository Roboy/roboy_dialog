package roboy.linguistics.sentenceanalysis;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import roboy.linguistics.Linguistics;
import roboy.linguistics.Linguistics.SentenceType;
import roboy.linguistics.Term;
import roboy.linguistics.Triple;
import roboy.util.Maps;

/**
 * Tries to find triples with rather stupid heuristics and stores the results in the
 * Linguistics.TRIPLE attribute of the interpretation.
 * 
 */
public class SentenceAnalyzer implements Analyzer{
	
	private Map<String, String> meanings;
	
	public SentenceAnalyzer () throws IOException, JsonSyntaxException, JsonIOException {
		// find meaning
		meanings = new HashMap<>();
		ClassLoader cl = this.getClass().getClassLoader();


		List<String> filenames = new ArrayList<>();
		String path = "ontology/terms/";
		Gson gson = new Gson();

		try(
			InputStream inputStream = cl.getResourceAsStream(path);
			BufferedReader br = new BufferedReader( new InputStreamReader( inputStream ) ) ) {
			String resource;

			while( (resource = br.readLine()) != null ) {
				InputStream is2 = cl.getResourceAsStream(path+resource);
				BufferedReader br2 = new BufferedReader(new InputStreamReader(is2));
				Term term = gson.fromJson(br2,Term.class);
				meanings.put(resource, term.getConcept());
				filenames.add( resource );
			}
		}
	}
	
	@Override
	public Interpretation analyze(Interpretation interpretation){
		String sentence = interpretation.getSentence();
		// NLP pipeline
		List<String> tokens = interpretation.getTokens();
		String[] posTags = interpretation.getPosTags();
		SentenceType sentenceType = interpretation.getSentenceType();
		
		Interpretation result = extractPAS(sentence, tokens, posTags, sentenceType); //TODO: avoid object creation
		interpretation.setTriples(result.getTriples());
		return interpretation;
	}
	
	
	private Interpretation extractPAS(String sentence, List<String> tokens, String[] posTags, SentenceType sentenceType){
//		System.out.println("  "+sentenceType);
        // TODO: Are you serious?
		switch(sentenceType){
			case STATEMENT: return new Interpretation(sentenceType,sentence,analyzeStatement(tokens, posTags));
			case IS_IT:     return new Interpretation(sentenceType,sentence,analyzeIsIt(tokens, posTags));
			case WHO:       return new Interpretation(sentenceType,sentence,analyzeWho(tokens, posTags));
			case HOW_IS:    return new Interpretation(sentenceType,sentence,analyzeHowIs(tokens, posTags));
			case HOW_DO:    return new Interpretation(sentenceType,sentence,analyzeHowDo(tokens, posTags));
			case WHAT:      return new Interpretation(sentenceType,sentence,analyzeWhat(tokens, posTags));
			case DOES_IT:   return new Interpretation(sentenceType,sentence,analyzeDoesIt(tokens, posTags));
			default:        return new Interpretation(sentenceType,sentence,new Triple(null,"",""));
		}
	}
	
	private Triple analyzeStatement(List<String> tokens, String[] posTags){
		String predicate = null;
		String agents = "";
		String patients = "";
		for(int i=0; i < tokens.size(); i++) {
			if(posTags[i].startsWith("V")){
				for(int j=0; j < i; j++){
					if(posTags[j].startsWith("N") || posTags[j].startsWith("J")) {
						if(j>0) agents += " ";
						agents += tokens.get(j);
					}
				}
				predicate = tokens.get(i);
				for(int j=i+1; j < tokens.size(); j++) {
					if(posTags[j].startsWith("N") || posTags[j].startsWith("J")) {
						if(j>i+1) patients += " ";
						patients += tokens.get(j);
					}
				}
			}
		}
		return new Triple(predicate, agents, patients);
	}
	
	private Triple analyzeIsIt(List<String> tokens, String[] posTags){
		String predicate = null;
		String agents = "";
		String patients = "";
		if (tokens.size() > 2) {
			predicate = tokens.get(0);
            agents = tokens.get(1);
			for(int j = 2; j < tokens.size(); j++) {
				if(j>2) patients += " ";
                patients += tokens.get(j);
			}
		}
		return new Triple(predicate, agents, patients);
	}
	
	private Triple analyzeDoesIt(List<String> tokens, String[] posTags){
        String predicate = null;
        String agents = "";
        String patients = "";
		if(tokens.size() > 3 && (posTags[3].startsWith("V") || posTags[2].startsWith("V"))){
			predicate = posTags[3].startsWith("V") ? tokens.get(3) : tokens.get(2);
            agents = tokens.get(1);
			for(int j = 3; j < tokens.size(); j++) {
				if(j > 3) patients += "";
                patients += tokens.get(j);
			}
		}
		return new Triple(predicate, agents, patients);
	}
	
	private Triple analyzeWho(List<String> tokens, String[] posTags){
        String predicate = null;
        String agents = "";
        String patients = "";
		if(tokens.size() > 2 && posTags[1].startsWith("V")) {
			patients = null;
			predicate = tokens.get(1);
			for(int j = 2; j < tokens.size(); j++) {
				if(j > 2) agents += " ";
				agents += tokens.get(j);
			}
		}
		return new Triple(predicate, agents, patients);
	}
	
	private Triple analyzeWhat(List<String> tokens, String[] posTags){
        String predicate = null;
        String agents = "";
        String patients = "";
		if(tokens.size() > 3 && posTags[3].startsWith("V")) {
			agents = tokens.get(2);
			predicate = tokens.get(3);
			patients = null;
		}
		return new Triple(predicate, agents, patients);
	}
	
	private Triple analyzeHowIs(List<String> tokens, String[] posTags){
		return analyzeWho(tokens, posTags);
	}

	private Triple analyzeHowDo(List<String> tokens, String[] posTags){
        String predicate = null;
        String agents = "";
        String patients = "";
		if(tokens.size() > 3 && posTags[3].startsWith("V")) {
			agents = tokens.get(2);
			predicate = tokens.get(3);
			for(int j = 4; j < tokens.size(); j++) {
				if(j > 4) patients += " ";
				patients += tokens.get(j);
			}
		}
		return new Triple(predicate, agents, patients);
	}
	
//	private int detectNP(String[] posTags){
//		
//	}
	
//	private int detectHeadVerb(String[] posTags){
//		
//	}
}
