package roboy.io;

import java.util.HashMap;
import java.util.Map;

import roboy.ros.RosMainNode;

/**
 * Using Bing to perform speech to text. Requires internet connection.
 */
public class BingInput implements InputDevice 
{
	private RosMainNode rosMainNode;

	public BingInput (RosMainNode node){
		this.rosMainNode = node;
}
	@Override
	public Input listen() throws InterruptedException 
	{
		HashMap<SpeakerInfo, String> input = rosMainNode.RecognizeSpeech();
		
		Map.Entry<SpeakerInfo, String> entry = input.entrySet().iterator().next();
		SpeakerInfo speakers = entry.getKey();
		String text = entry.getValue();

		System.out.println(text); 
		return new Input(text, speakers);
	}
	
}
