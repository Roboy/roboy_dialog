package roboy.memory.nodes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class represents a full node similarly to its representation in Memory.
 */
public class MemoryNodeModel {
    //Unique node IDs assigned by the memory.
    private int id;
    //"Person" etc.
    private ArrayList<String> labels;
    //"Person" etc. Duplicate because Memory expects a single Label in CREATE queries, but
    // returns an array of labels inside GET responses.
    private String label;
    //name, birthdate
    private HashMap<String, Object> properties;
    //Relation: <name as String, ArrayList of IDs (nodes related to this node over this relation)>
    private HashMap<String, ArrayList<Integer>> relationships;
    //If true, then fields with default values will be removed from JSON format.
    // Transient as stripping information is not a part of the node and not included in query.
    transient boolean stripQuery = false;

    public MemoryNodeModel(){
        this.id = 0;
    }

    public MemoryNodeModel(boolean stripQuery) {
        if(!stripQuery) {
            id = 0;
            labels = new ArrayList<>();
            properties = new HashMap<>();
            relationships = new HashMap<>();
        } else {
            id = 0;
            this.stripQuery = true;
        }
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<String> getLabels() {
        return labels;
    }
    public void setLabel(String label) {
        if(this.labels == null) {
            this.labels = new ArrayList<>();
        }
        labels.add(label);
        this.label = label;
    }

    public HashMap<String, Object> getProperties() {
        return properties;
    }
    public Object getProperty(String key) {
        return (properties != null ? properties.get(key) : null);
    }

    public void setProperties(HashMap<String, Object> properties) {
        if(this.properties == null) {
            this.properties = new HashMap<>();
        }
        this.properties.putAll(properties);
    }
    public void setProperty(String key, Object property) {
        if(this.properties == null) {
            this.properties = new HashMap<>();
        }
        properties.put(key, property);
    }

    public HashMap<String, ArrayList<Integer>> getRelationships() {
        return relationships;
    }
    public ArrayList<Integer> getRelationship(String key) {
        //TODO: Sort this shit out
        //return (relationships != null ? relationships.get(key.toLowerCase()) : null);
        return (relationships != null ? relationships.get(key) : null);
    }
    public void setRelationships(HashMap<String, ArrayList<Integer>> relationships) {
        if(this.relationships == null) {
            this.relationships = new HashMap<>();
        }
        this.relationships.putAll(relationships);
    }
    public void setRelationship(String key, Integer id) {
        if(this.relationships == null) {
            this.relationships = new HashMap<>();
        }
        if(relationships.containsKey(key)) {
            relationships.get(key).add(id);
        } else {
            ArrayList idList = new ArrayList();
            idList.add(id);
            relationships.put(key, idList);
        }
    }

    public void setStripQuery(boolean strip) {
        this.stripQuery = strip;
    }

    /**
     * This toString method returns the whole object, including empty variables.
     */
    public String toJSON(Gson gson){
        String json = gson.toJson(this);
        if(stripQuery) {
            //This is based on https://stackoverflow.com/questions/23920740/remove-empty-collections-from-a-json-with-gson
            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String,Object> obj = gson.fromJson(json, type);
            for(Iterator<Map.Entry<String, Object>> it = obj.entrySet().iterator(); it.hasNext();) {
                Map.Entry<String, Object> entry = it.next();
                if (entry.getValue() == null) {
                    it.remove();
                } else if (entry.getValue().getClass().equals(ArrayList.class)) {
                    if (((ArrayList<?>) entry.getValue()).size() == 0) {
                        it.remove();
                    }
                    //As ID is parsed into Double inside GSON, usng Double.class
                } else if (entry.getValue().getClass().equals(Double.class)) {
                    if (((Double) entry.getValue()) == 0) {
                        it.remove();
                    }
                } else if (entry.getValue().getClass().equals(HashMap.class)) {
                    if (((HashMap<?,?>) entry.getValue()).size() == 0) {
                        it.remove();
                    }
                } else if (entry.getValue().getClass().equals(String.class)) {
                    if (((String) entry.getValue()).equals("")) {
                        it.remove();
                    }
                }
            }
            json = gson.toJson(obj);
        }
        return json;
    }
    /**
     * Returns an instance of this class based on the given JSON.
     */
    public MemoryNodeModel fromJSON(String json, Gson gson) {
        return gson.fromJson(json, this.getClass());
    }
}
