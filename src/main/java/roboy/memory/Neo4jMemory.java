package roboy.memory;
import org.json.*;

import roboy.linguistics.Triple;
import roboy.ros.RosMainNode;

import java.io.IOException;
import java.util.*;

/**
 * Implements the high-level-querying tasks to the Memory services using RosMainNode.
 * It is possible to use either direct querying via JSON or query building methods.
 */
public class Neo4jMemory implements Memory<JSONObject>
{
    private RosMainNode rosMainNode;

    public Neo4jMemory (RosMainNode node){
        this.rosMainNode = node;
    }

    @Override
    public boolean save(JSONObject query) throws InterruptedException, IOException
    {
        return rosMainNode.UpdateMemoryQuery(query.toString())!=null;
    }

    @Override
    public List<JSONObject> retrieve(JSONObject query) throws InterruptedException, IOException
    {
        List<JSONObject> result = new ArrayList<>();
        result.add(new JSONObject(rosMainNode.GetMemoryQuery(query.toString())));
        return result;
    }

    public List<JSONObject> create(JSONObject query) throws InterruptedException, IOException
    {
        List<JSONObject> result = new ArrayList<>();
        result.add(new JSONObject(rosMainNode.CreateMemoryQuery(query.toString())));
        return result;
    }

    public List<JSONObject> remove(JSONObject query) throws InterruptedException, IOException
    {
        List<JSONObject> result = new ArrayList<>();
        result.add(new JSONObject(rosMainNode.DeleteMemoryQuery(query.toString())));
        return result;
    }

    private String getIdOfPersonByName(String name) {
        JSONObject query = new JSONObject();
        //"{'label':'Person', relations:{'FRIEND_OF':[15]}, 'properties':{'name':'Laura'}}\""
        query.put("label","Person");
        JSONObject props = new JSONObject();
        props.put("name", name);
        query.put("properties", props);
        try {
            List<JSONObject> result = retrieve(query);
            if(isValid(result)) return Integer.toString(result.get(0).getInt("id"));
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String createNewPersonNode(String name) {
        JSONObject query = new JSONObject();
        //"{'label':'Person', relations:{'FRIEND_OF':[15]}, 'properties':{'name':'Laura'}}\""
        query.put("label","Person");
        JSONObject props = new JSONObject();
        props.put("name", name);
        query.put("properties", props);
        try {
            return Integer.toString((create(query)).get(0).getInt("id"));
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getPersonId(String name) {
        String id = getIdOfPersonByName(name);
        if(id == null) {
            id = createNewPersonNode(name);
        }
        return id;
    }

    public JSONObject getPersonInformation(String id) {
        JSONObject query = new JSONObject();
        query.append("id", id);
        List<JSONObject> result = null;
        try {
            result = retrieve(query);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        if(isValid(result)) return result.get(0);
        return null;
    }

    public boolean addPersonInformation(String property, String value) {
        JSONObject query = new JSONObject();
        query.append(property, value);
        try {
            return save(query);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isValid(List<JSONObject> result) {
        return (!result.isEmpty()) && !(result.get(0) == null);
    }

}
