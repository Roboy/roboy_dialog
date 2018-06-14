package roboy.linguistics;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


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

    @Override
    public String toString() {
        return "Entity{" +
                "forms=" + forms +
                '}';
    }

    @Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
		    return false;
        }

		Entity comparableObject = (Entity) obj;
		return Objects.equals(getForms(), comparableObject.getForms());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getForms());
	}
}
