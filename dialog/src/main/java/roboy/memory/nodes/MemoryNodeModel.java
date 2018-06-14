package roboy.memory.nodes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.memory.Neo4jLabel;
import roboy.memory.Neo4jMemoryInterface;
import roboy.memory.Neo4jProperty;
import roboy.memory.Neo4jRelationship;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class represents a full node similarly to its representation in Memory.
 */
public class MemoryNodeModel {
    final static Logger logger = LogManager.getLogger();
    protected Neo4jMemoryInterface memory;
    //Unique node IDs assigned by the memory.
    private int id;
    //"Person" etc.
    @Expose
    private ArrayList<String> labels;
    //"Person" etc. Duplicate because Memory expects a single Label in CREATE queries, but
    // returns an array of labels inside GET responses.
    @Expose
    private Neo4jLabel label;
    //name, birthdate
    @Expose
    private HashMap<String, Object> properties;
    //Relation: <name as String, ArrayList of IDs (nodes related to this node over this relation)>
    @Expose
    private HashMap<String, ArrayList<Integer>> relationships;
    //If true, then fields with default values will be removed from JSON format.
    // Transient as stripping information is not a part of the node and not included in query.
    @Expose
    transient boolean stripQuery = false;

    public MemoryNodeModel(Neo4jMemoryInterface memory){
        this.memory = memory;
        // TODO why id is assigned here?
        this.id = 0;
    }

    public MemoryNodeModel(String jsonString, Neo4jMemoryInterface memory){
        this.memory = memory;
        MemoryNodeModel node = fromJSON(jsonString, new Gson());
        setId(node.getId());
        setRelationships(node.getRelationships() != null ? node.getRelationships() : new HashMap<>());
        setProperties(node.getProperties() != null ? node.getProperties() : new HashMap<>());
    }

    public MemoryNodeModel(boolean stripQuery, Neo4jMemoryInterface memory) {
        this.memory = memory;
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

    public ArrayList<Neo4jLabel> getLabels() {
        ArrayList<Neo4jLabel> neo4jLabels = new ArrayList<>();
        if (labels != null) {
            for (String label : labels) {
                Neo4jLabel neo4jLabel = Neo4jLabel.lookupByType(label);
                if (neo4jLabel != null) {
                    neo4jLabels.add(neo4jLabel);
                }
            }
        }
        return neo4jLabels;
    }
    public void setLabel(Neo4jLabel label) {
        if(this.labels == null) {
            this.labels = new ArrayList<>();
        }
        labels.add(label.type);
        this.label = label;
    }

    public HashMap<Neo4jProperty, Object> getProperties() {
        HashMap<Neo4jProperty, Object> neo4jProperties = new HashMap<>();
        if (properties != null) {
            for (String key : properties.keySet()) {
                Neo4jProperty neo4jProperty = Neo4jProperty.lookupByType(key);
                if (neo4jProperty != null) {
                    neo4jProperties.put(neo4jProperty, properties.get(key));
                }
            }
        }
        return neo4jProperties;
    }

    public Object getProperty(Neo4jProperty key) {
        return (properties != null ? properties.get(key.type) : null);
    }

    public void setProperties(HashMap<Neo4jProperty, Object> properties) {
        if(this.properties == null) {
            this.properties = new HashMap<>();
        }
        for (Neo4jProperty key : properties.keySet()) {
            setProperty(key, properties.get(key));
        }
    }

    public void setProperty(Neo4jProperty key, Object property) {
        if(this.properties == null) {
            this.properties = new HashMap<>();
        }
        properties.put(key.type, property);
    }

    public HashMap<Neo4jRelationship, ArrayList<Integer>> getRelationships() {
        HashMap<Neo4jRelationship, ArrayList<Integer>> neo4jRelationships = new HashMap<>();
        if (relationships != null) {
            for (String key : relationships.keySet()) {
                Neo4jRelationship neo4jRelationship = Neo4jRelationship.lookupByType(key);
                if (neo4jRelationship != null) {
                    neo4jRelationships.put(neo4jRelationship, relationships.get(key));
                }
            }
        }
        return neo4jRelationships;
    }

    public ArrayList<Integer> getRelationship(Neo4jRelationship key) {
        //TODO: Sort this shit out
        //return (relationships != null ? relationships.get(key.toLowerCase()) : null);
        return (relationships != null ? relationships.get(key.type) : null);
    }

    public void setRelationships(HashMap<Neo4jRelationship, ArrayList<Integer>> relationships) {
        if(this.relationships == null) {
            this.relationships = new HashMap<>();
        }
        for (Neo4jRelationship key : relationships.keySet()) {
            setRelationships(key, relationships.get(key));
        }
    }

    public void setRelationships(Neo4jRelationship key, ArrayList<Integer> ids) {
        if(this.relationships == null) {
            this.relationships = new HashMap<>();
        }
        if(relationships.containsKey(key.type)) {
            relationships.get(key.type).addAll(ids);
        } else {
            relationships.put(key.type, ids);
        }
    }

    public void setRelationship(Neo4jRelationship key, Integer id) {
        if(this.relationships == null) {
            this.relationships = new HashMap<>();
        }
        if(relationships.containsKey(key.type)) {
            relationships.get(key.type).add(id);
        } else {
            ArrayList<Integer> idList = new ArrayList<>();
            idList.add(id);
            relationships.put(key.type, idList);
        }
    }

    public void setStripQuery(boolean strip) {
        this.stripQuery = strip;
    }

    /**
     * This toString method returns the whole object, including empty variables.
     */
    public String toJSON(){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String json;
        // ID of 0 is default ID for an uncertain node.
        // If there exists a valid non-default ID value obtained from memory, it has to be included
        if (getId() != 0) {
            JsonElement jsonElement = gson.toJsonTree(this);
            jsonElement.getAsJsonObject().addProperty("id", getId());
            json = gson.toJson(jsonElement);
        } else {
            json = gson.toJson(this);
        }
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

    @Override
    public String toString() {
        return "MemoryNodeModel{" +
                "memory=" + memory +
                ", id=" + id +
                ", labels=" + labels +
                ", label=" + label +
                ", properties=" + properties +
                ", relationships=" + relationships +
                ", stripQuery=" + stripQuery +
                '}';
    }
}
