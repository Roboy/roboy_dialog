package roboy.memory.nodes;

import org.json.JSONObject;

import java.util.Iterator;


/**
 * This class does not include empty fields in the JSON query.
 */

@Deprecated
public class StrippedQuery extends MemoryNodeModel {
    public String toString(){
       // this.labels = null;
        JSONObject object = new JSONObject(this);
        for(Iterator it = object.keys(); it.hasNext();) {
            String key = it.next().toString();
            if(object.get(key) == null || object.get(key).toString() == "" ||object.get(key).toString() == "-1") {
                it.remove();
            }
        }
        return object.toString();
    }
}
