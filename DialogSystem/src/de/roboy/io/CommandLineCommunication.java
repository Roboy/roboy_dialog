package de.roboy.io;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import de.roboy.dialog.action.Action;
import de.roboy.dialog.action.ShutDownAction;
import de.roboy.dialog.action.SpeechAction;
import de.roboy.dialog.personality.Personality;
import de.roboy.linguistics.sentenceanalysis.Interpretation;
import de.roboy.linguistics.sentenceanalysis.SentenceAnalyzer;

@Deprecated
public class CommandLineCommunication implements Communication{

	private Personality personality;
	private SentenceAnalyzer analyzer;
	
	@Override
	public void setPersonality(Personality p) {
		personality = p;
		try {
			analyzer = new SentenceAnalyzer();
		} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void communicate() {
		Scanner sc = new Scanner(System.in);
		String raw = sc.nextLine();
		Interpretation input = analyzer.analyze(raw);
		List<Action> roboy =  personality.answer(input);
		while(roboy.size()==1 && !(roboy.get(0) instanceof ShutDownAction)){
			talk(roboy);
			raw = sc.nextLine();
			input = analyzer.analyze(raw);
			roboy = personality.answer(input);
		}
		List<Action> lastwords = ((ShutDownAction)roboy.get(0)).getLastWords();
		talk(lastwords);
	    sc.close();
	}
	
	private void talk (List<Action> actions) {
		for(Action a : actions){
			if(a instanceof SpeechAction){
				System.out.println(((SpeechAction) a).getText());
			}
		}
	}

}
