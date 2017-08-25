package roboy.memory.nodes;

import org.json.JSONObject;

import java.util.Iterator;

/**
 * This is a helper class to send generic GET queries to Neo4jNode.
 * Any property which is not set will not be included in the query JSON.
 */
@Deprecated
public class GetQueryTemplate extends MemoryNodeModel {
    @Override
    public String toString(){
        //this.labels = null;
        JSONObject object = new JSONObject(this);
        for(Iterator it = object.keys(); it.hasNext();) {
            String key = it.next().toString();
            Object field = object.get(key);
            if(field == null || field.toString().equals("") || field.toString().equals("-1") || field.toString().equals("{}")) {
                it.remove();
            }
        }
        return object.toString();
    }
}
