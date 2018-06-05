package roboy.linguistics;

import java.util.HashMap;
import java.util.Map;

public class Entity {
	
	private Map<String,String> forms;
	
	public Entity(String term){
		forms = new HashMap<>();
		forms.put("base", term);
	}

    public Entity(String key, String term){
        forms = new HashMap<>();
        forms.put(key, term);
    }
	
	public String getForm(String form){
		return forms.get(form);
	}

	public String getBaseForm(){
		return getForm("base");
	}
	
	public Map<String,String> getForms(){
		return forms;
	}
}
