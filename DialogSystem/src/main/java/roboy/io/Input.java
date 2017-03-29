package roboy.io;

import java.util.HashMap;
import java.util.Map;

public class Input {
	
	public String sentence;
	public Map<String,Object> attributes;
	
	public Input(String sentence){
		this.sentence = sentence;
		attributes = new HashMap<>();
	}
	
	public Input(String sentence, Map<String,Object> attributes){
		this.sentence = sentence;
		this.attributes = attributes;
	}

}
