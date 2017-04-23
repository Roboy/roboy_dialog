package roboy.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import roboy.dialog.action.Action;
import roboy.dialog.action.ShutDownAction;
import roboy.dialog.personality.CuriousPersonality;
import roboy.dialog.personality.DefaultPersonality;
import roboy.dialog.personality.KnockKnockPersonality;
import roboy.dialog.personality.Personality;
import roboy.dialog.personality.SmallTalkPersonality;

import roboy.io.BingInput;
import roboy.io.BingOutput;
import roboy.io.CelebritySimilarityInput;
import roboy.io.CommandLineInput;
import roboy.io.CommandLineOutput;
import roboy.io.EmotionOutput;
import roboy.io.Input;
import roboy.io.CerevoiceOutput;
import roboy.io.InputDevice;
import roboy.io.MultiInputDevice;
import roboy.io.MultiOutputDevice;
import roboy.io.OutputDevice;
import roboy.io.RoboyNameDetectionInput;
import roboy.io.Vision;

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
import edu.wpi.rail.jrosbridge.Ros;

public class TestVision { // TODO: This should go to test/java

	private static Ros start_rosbridge()
	{
		Ros ros = new Ros("localhost");
	    ros.connect();
	    System.out.println("ROS bridge is set up");	
	    return ros;	
	}

	public static void main(String[] args)
	{
		Ros ros = start_rosbridge();
		Vision v = new Vision();
		v.findFaces();

		
		
	}

}