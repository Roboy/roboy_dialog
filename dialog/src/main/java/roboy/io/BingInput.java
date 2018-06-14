package roboy.io;

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
	public Input listen()
	{
		String text = rosMainNode.RecognizeSpeech();
	    System.out.println(text);
		return new Input(text);
	}
	
}
