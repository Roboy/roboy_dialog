package roboy.dialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import roboy.dialog.action.Action;
import roboy.dialog.action.FaceAction;
import roboy.dialog.action.ShutDownAction;
import roboy.dialog.personality.CuriousPersonality;
import roboy.dialog.personality.DefaultPersonality;
import roboy.dialog.personality.KnockKnockPersonality;
import roboy.dialog.personality.Personality;
import roboy.dialog.personality.SmallTalkPersonality;

import roboy.io.*;

import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Analyzer;
import roboy.linguistics.sentenceanalysis.DictionaryBasedSentenceTypeDetector;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.linguistics.sentenceanalysis.OntologyNERAnalyzer;
import roboy.linguistics.sentenceanalysis.OpenNLPPPOSTagger;
import roboy.linguistics.sentenceanalysis.OpenNLPParser;
import roboy.linguistics.sentenceanalysis.SentenceAnalyzer;
import roboy.linguistics.sentenceanalysis.SimpleTokenizer;
import roboy.talk.Verbalizer;

import roboy.memory.RoboyMind;

import roboy.util.Concept;
import roboy.util.Ros;

public class DialogSystem {
	
	public static void main(String[] args) throws JsonIOException, IOException, InterruptedException {
		// RoboyMind memory = new RoboyMind(ros);
		// Map<String, Object> attributes = new HashMap<String, Object>(){{
		// 	put("class_name", "Person"); 
		// 	put("id", 0); 
		// 	put("name", "John"); 
		// 	put("occupation", "student");
		// }};
		// Concept object1 = new Concept(attributes);
		// attributes.put("name", "Anna");
		// attributes.put("id", 1);
		// Concept object2 = new Concept(attributes);
		// memory.save(object1);
		// memory.save(object2);
		// Map<String, Object> attributes1 = new HashMap<String, Object>(){{
		// 	put("occupation", "student");
		// }};
		// Concept object3 = new Concept(attributes1);
		// List<Concept> found_objects = memory.retrieve(object3);

		// Personality p = new DefaultPersonality();
//		Personality p = new CuriousPersonality();
//		Personality p = new KnockKnochPersonality();

		
		InputDevice input = new CommandLineInput();
		// InputDevice input = new BingInput();
		InputDevice celebInput = new CelebritySimilarityInput();
		InputDevice roboyDetectInput = new RoboyNameDetectionInput();
		InputDevice multiIn = new MultiInputDevice(input);//, celebInput, roboyDetectInput);
		
		//OutputDevice output = new CerevoiceOutput();
		// OutputDevice output = new BingOutput();

		EmotionOutput emotion = new EmotionOutput();
//        OutputDevice output = new CommandLineOutput(emotion);
        OutputDevice output = new CerevoiceOutput(emotion);
		OutputDevice multiOut = new MultiOutputDevice(output,emotion);
		
		List<Analyzer> analyzers = new ArrayList<Analyzer>();
		analyzers.add(new SimpleTokenizer());
		analyzers.add(new OpenNLPPPOSTagger());
		analyzers.add(new DictionaryBasedSentenceTypeDetector());
		analyzers.add(new SentenceAnalyzer());
		analyzers.add(new OpenNLPParser());
		analyzers.add(new OntologyNERAnalyzer());
		
		System.out.println("Initialized...");

        Vision vision = new Vision();

        while(true) {

            while (!vision.findFaces()) {
                emotion.act(new FaceAction("angry"));
            }
//            emotion.act(new FaceAction("neutral"));

//            while (!multiIn.listen().attributes.containsKey(Linguistics.ROBOYDETECTED)) {
//            }

            Input raw; //  = input.listen();
            Interpretation interpretation; // = analyzer.analyze(raw);
            Personality p = new SmallTalkPersonality(new Verbalizer());
            List<Action> actions = p.answer(new Interpretation(""));
            while (actions.isEmpty() || !(actions.get(0) instanceof ShutDownAction)) {
                multiOut.act(actions);
                raw = multiIn.listen();
                interpretation = new Interpretation(raw.sentence, raw.attributes);
                for (Analyzer a : analyzers) {
                    interpretation = a.analyze(interpretation);
                }
                actions = p.answer(interpretation);
            }
            List<Action> lastwords = ((ShutDownAction) actions.get(0)).getLastWords();
            multiOut.act(lastwords);
        }
	}

}
