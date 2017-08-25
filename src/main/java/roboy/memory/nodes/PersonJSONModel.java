package roboy.memory.nodes;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

@Deprecated
public class PersonJSONModel extends MemoryNodeModel {
    public String label = "Person";
    private int id;
    private String name;
    private HashMap<String, ArrayList<Integer>> relations;
    private HashMap<String, Object> properties;

    public static void main(String[] args) {
        PersonJSONModel test = new PersonJSONModel();
        System.out.println(test.toString());
    }

    /**
     * This object encapsulates JSON parsing for nodes with the Person label.
     */
    public PersonJSONModel() {
        id = 0;
        name = null;
        properties =  new HashMap<>();
        properties.put("birthday", "01.01.1999");
        properties.put("birthdayAsInt", 10111999);
        relations = new HashMap<>();
        ArrayList<Integer> home = new ArrayList<>();
        home.add(28);
        home.add(23);
        relations.put("LIVE_IN", home);
    }

    //If a value should show up in the JSON representation, it needs a public setter (I guess).
    public String getLabel() {
        return label;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public HashMap<String, Object> getProperties() {
        return properties;
    }
    public void setProperty(String key, Object property) {
        properties.put(key, property);
    }
    public void removeProperty(String key) {
        properties.remove(key);
    }
    public HashMap<String, ArrayList<Integer>> getRelations() {
        return relations;
    }

    /**
     * This toString method removes all entries with value <code>null</code> or "" value.
     */
    public String toString(){
        JSONObject object = new JSONObject(this);
        for(Iterator it = object.keys(); it.hasNext();) {
            String key = it.next().toString();
            if(object.get(key) == null || object.get(key).toString() == "") {
                it.remove();
            }
        }
        return object.toString();
    }
}
