package roboy.linguistics.sentenceanalysis;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import roboy.linguistics.Linguistics;

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
		
		return interpretation;
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
		Interpretation i = new Interpretation("The man saw the boy with the binoculars.");
		new OpenNLPParser().analyze(i);
	}

}
