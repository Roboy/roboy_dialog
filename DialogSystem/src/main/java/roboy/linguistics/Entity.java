package roboy.linguistics;

import java.util.HashMap;
import java.util.Map;

public class Entity {
	
	private Map<String,String> forms;
	
	public Entity(String term){
		forms = new HashMap<>();
		forms.put("base", term);
	}
	
	public String getForm(String form){
		return forms.get(form);
	}
	
	public Map<String,String> getForms(){
		return forms;
	}
}
