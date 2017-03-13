package roboy.linguistics.sentenceanalysis;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import roboy.linguistics.Linguistics;
import roboy.linguistics.Linguistics.SEMANTIC_ROLE;

public class OpenNLPParser implements Analyzer{
	
	private Parser parser;
	
	public OpenNLPParser(){
		InputStream modelIn=null;
		try {
			modelIn = new FileInputStream("resources/en-parser-chunking.bin");
			ParserModel model = new ParserModel(modelIn);
			parser = ParserFactory.create(model);
		}
		catch (IOException e) {
		  e.printStackTrace();
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

	@Override
	public Interpretation analyze(Interpretation interpretation) {
		String sentence = (String) interpretation.getFeatures().get(Linguistics.SENTENCE);
		Parse parse = ParserTool.parseLine(sentence, parser, 1)[0];
		interpretation = extractPAS(interpretation,parse);
		return interpretation;
	}
	
	private Interpretation extractPAS(Interpretation interpretation, Parse parse){
		System.out.println(parseToString(parse,0));
		System.out.println(pas(parse));
		return interpretation;
	}
	
	private Map<SEMANTIC_ROLE,Object> pas(Parse parse){
		Map<SEMANTIC_ROLE,Object> result = new HashMap<>();
		Parse[] children = parse.getChildren();
		boolean modverb = false;
		Object subject = null;
		Object object = null;
		String verb = null;
		for(int i=0; i<children.length; i++){
			if("NP".equals(children[i].getType())){
				subject = children[i].toString();
			} else if("VP".equals(children[i].getType())){
				Parse[] vpChildren = children[i].getChildren();
				for(int j=0; j<vpChildren.length; j++){
					if("NP".equals(vpChildren[j].getType())){
						object = vpChildren[j].toString();
					} else if("VP".equals(vpChildren[j].getType())){
						modverb = true;
						Parse[] vp2Children = vpChildren[j].getChildren();
						for(int k=0; k<vpChildren.length; k++){
							if("NP".equals(vp2Children[k].getType())){
								object = vp2Children[k].toString();
							} else if(vp2Children[k].getType().startsWith("V")){
								result.put(SEMANTIC_ROLE.PREDICATE, vp2Children[k].toString());
							} else if("SBAR".equals(vp2Children[k].getType())){
								Parse[] sbarChildren = vp2Children[k].getChildren();
								for(int l=0; l<sbarChildren.length; l++){
									if("S".equals(sbarChildren[l].getType())){
										result.put(SEMANTIC_ROLE.PATIENT, pas(sbarChildren[l]));
									}
								}
							} else if("S".equals(vp2Children[k].getType())){
								result.put(SEMANTIC_ROLE.PATIENT, pas(vp2Children[k]));
							}
						}
					} else if(vpChildren[j].getType().startsWith("V")){
						verb = vpChildren[j].toString();
					} else if("SBAR".equals(vpChildren[j].getType())){
						Parse[] sbarChildren = vpChildren[j].getChildren();
						for(int l=0; l<sbarChildren.length; l++){
							if("S".equals(sbarChildren[l].getType())){
								result.put(SEMANTIC_ROLE.PATIENT, pas(sbarChildren[l]));
							}
						}
					} else if("S".equals(vpChildren[j].getType())){
						result.put(SEMANTIC_ROLE.PATIENT, pas(vpChildren[j]));
					}
				}
			} else if("S".equals(children[i].getType())){
				result = pas(children[i]); // TODO: multiple PAS per sentence
			}
		}
		// check passive
		if(verb!=null){
			if(modverb && Linguistics.tobe.contains(verb.toLowerCase())){
				if(subject!=null) result.put(SEMANTIC_ROLE.PATIENT, subject);
				if(object!=null) result.put(SEMANTIC_ROLE.AGENT, object);
			} else {
				if(subject!=null) result.put(SEMANTIC_ROLE.AGENT, subject);
				if(object!=null) result.put(SEMANTIC_ROLE.PATIENT, object);
			}
		}
		if(!result.containsKey(SEMANTIC_ROLE.PREDICATE) && verb!=null){
			result.put(SEMANTIC_ROLE.PREDICATE, verb);
		}
		
		return result;
	}
	
	public StringBuilder parseToString(Parse parse, int offset){
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<offset; i++) sb.append(" ");
		sb.append(parse.getType());
		sb.append(": ");
		sb.append(parse);
		sb.append("\n");
		for(Parse child : parse.getChildren()){
			sb.append(parseToString(child,offset+2));
		}
		return sb;
	}
	
	public static void main(String[] args) {
//		Interpretation i = new Interpretation("The man is seen by the boy with the binoculars.");
		Interpretation i = new Interpretation("Bill said that Mike like to play the piano.");
		new OpenNLPParser().analyze(i);
	}

}
