package roboy.memory.nodes;

import org.json.JSONObject;

/**
 * Get query with only the ID set.
 * This causes memory to return a single node with the specified ID.
 *
 * Example:
 * {"id":10}
 */
@Deprecated
public class RetrieveQueryTemplate extends MemoryNodeModel {
    public RetrieveQueryTemplate(int id) {
       // this.id = id;
    }
    public String toString(){
        JSONObject object = new JSONObject();
        //object.put("id", id);
        return object.toString();
    }
}
