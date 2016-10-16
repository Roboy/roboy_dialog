package de.roboy.linguistics.sentenceanalysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import de.roboy.linguistics.Term;
import de.roboy.linguistics.Triple;
import de.roboy.linguistics.sentenceanalysis.Sentence.SENTENCE_TYPE;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

public class SentenceAnalyzer {
	
	private Map<String,String> meanings;
	
	public SentenceAnalyzer () throws JsonSyntaxException, JsonIOException, FileNotFoundException{
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
	}
	
	public Sentence analyze(String sentence){
		// NLP pipeline
		String[] tokens = tokenize(sentence);
		String[] posTags = posTag(tokens);
		SENTENCE_TYPE sentenceType = determineSentenceType(tokens, posTags);
		return extractPAS(tokens, posTags, sentenceType);
	}
	
	private String[] tokenize(String sentence){
		return sentence.split("\\s+");
	}
	
	private String[] posTag(String[] tokens){
		InputStream modelIn = null;

		try {
		  modelIn = new FileInputStream("resources/en-pos-maxent.bin");
		  POSModel model = new POSModel(modelIn);
		  POSTaggerME tagger = new POSTaggerME(model);
		  return tagger.tag(tokens);
		}
		catch (IOException e) {
		  // Model loading failed, handle the error
		  e.printStackTrace();
		  return null;
		}
		finally {
		  if (modelIn != null) {
		    try {
		      modelIn.close();
		    }
		    catch (IOException e) {
		    }
		  }
		}
	}

	private SENTENCE_TYPE determineSentenceType(String[] tokens, String[] posTags){
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
	
	private Sentence extractPAS(String[] tokens, String[] posTags, SENTENCE_TYPE sentenceType){
		switch(sentenceType){
			case STATEMENT: return new Sentence(analyzeStatement(tokens, posTags),sentenceType);
			case DOES_IT:   return new Sentence(analyzeDoesIt(tokens, posTags),sentenceType);
			case WHO:       return new Sentence(analyzeWho(tokens, posTags),sentenceType);
			case HOW:       return new Sentence(analyzeHow(tokens, posTags),sentenceType);
			default:        return new Sentence(new Triple(null,"",""),sentenceType);
		}
	}
	
	private Triple analyzeStatement(String[] tokens, String[] posTags){
		String predicate = null;
		String agens = "";
		String patiens = "";
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
		return new Triple(predicate,agens,patiens);
	}
	
	private Triple analyzeDoesIt(String[] tokens, String[] posTags){
		String predicate = null;
		String agens = "";
		String patiens = "";
		if(tokens.length>2 && meanings.containsKey(tokens[0].toLowerCase())){
			predicate = tokens[0];
			agens = tokens[1];
			for(int j=2; j<tokens.length; j++){
				if(j>2) patiens+=" ";
				patiens+=tokens[j];
			}
		}
		return new Triple(predicate,agens,patiens);
	}
	
	private Triple analyzeWho(String[] tokens, String[] posTags){
		String predicate = null;
		String agens = "";
		String patiens = "";
		if(tokens.length>2 && meanings.containsKey(tokens[1].toLowerCase())){
			agens = null;
			predicate = tokens[1];
			for(int j=2; j<tokens.length; j++){
				if(j>2) patiens+=" ";
				patiens+=tokens[j];
			}
		}
		return new Triple(predicate,agens,patiens);
	}
	
	private Triple analyzeHow(String[] tokens, String[] posTags){
		String predicate = null;
		String agens = "";
		String patiens = "";
		if(tokens.length>2 && meanings.containsKey(tokens[1].toLowerCase())){
			patiens = null;
			predicate = tokens[1];
			for(int j=2; j<tokens.length; j++){
				if(j>2) agens+=" ";
				agens+=tokens[j];
			}
		}
		return new Triple(predicate,agens,patiens);
	}
	
}
