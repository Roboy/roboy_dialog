package de.roboy.dialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import de.roboy.dialog.action.Action;
import de.roboy.dialog.action.ShutDownAction;
import de.roboy.dialog.personality.CuriousPersonality;
import de.roboy.dialog.personality.DefaultPersonality;
import de.roboy.dialog.personality.KnockKnockPersonality;
import de.roboy.dialog.personality.Personality;
import de.roboy.dialog.personality.SmallTalkPersonality;
import de.roboy.io.BingInput;
import de.roboy.io.CommandLineInput;
import de.roboy.io.CommandLineOutput;
import de.roboy.io.InputDevice;
import de.roboy.io.OutputDevice;
import de.roboy.linguistics.sentenceanalysis.Analyzer;
import de.roboy.linguistics.sentenceanalysis.Interpretation;
import de.roboy.linguistics.sentenceanalysis.OntologyNERAnalyzer;
import de.roboy.linguistics.sentenceanalysis.OpenNLPPPOSTagger;
import de.roboy.linguistics.sentenceanalysis.SentenceAnalyzer;
import de.roboy.linguistics.sentenceanalysis.SimpleTokenizer;
import de.roboy.talk.Verbalizer;

public class DialogSystem {
	
	public static void main(String[] args) throws JsonSyntaxException, JsonIOException, IOException, InterruptedException {
//		Personality p = new DefaultPersonality();
//		Personality p = new CuriousPersonality();
//		Personality p = new KnockKnochPersonality();
		Personality p = new SmallTalkPersonality(new Verbalizer());
		
//		InputDevice input = new CommandLineInput();
		InputDevice input = new BingInput();
		OutputDevice output = new CommandLineOutput();
		List<Analyzer> analyzers = new ArrayList<Analyzer>();
		analyzers.add(new SimpleTokenizer());
		analyzers.add(new OpenNLPPPOSTagger());
		analyzers.add(new SentenceAnalyzer());
		analyzers.add(new OntologyNERAnalyzer());
		
		String raw; //  = input.listen();
		Interpretation interpretation; // = analyzer.analyze(raw);
		List<Action> actions =  p.answer(new Interpretation(""));
		while(actions.size()>=1 && !(actions.get(0) instanceof ShutDownAction)){
			output.act(actions);
			raw = input.listen();
			interpretation = new Interpretation(raw);
			for(Analyzer a : analyzers){
				interpretation = a.analyze(interpretation);
			}
			actions = p.answer(interpretation);
		}
		List<Action> lastwords = ((ShutDownAction)actions.get(0)).getLastWords();
		output.act(lastwords);
	}

}
