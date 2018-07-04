package roboy.memory;


import org.roboy.memory.util.MemoryOperations;

/**
 * This Class creates an interface to connect to memory.
 * Instead of calling via a service via ROS, we simply call the function directly and get returned a JSON string.
 */
public class Neo4jMemoryOperations {

    /**
     * Get the Node ID
     * @param query Query to specify Node to get. Ex: {"labels":["Person"],"label":"Person","properties":{"name":"davis"}}
     * @return JSON containing ID of node
     */
    public static String get   (String query) { return MemoryOperations.get(query);    }

    /**
     * Cypher Method that is never called
     * TODO: Implement this feature or refactor it out, it's kind of here because there was a service
     * @param query
     * @return
     */
    public static String cypher(String query) { return MemoryOperations.cypher(query); }

    /**
     * Create a node
     * @param query Query with data regarding the node. Ex: {"labels":["Organization"],"label":"Organization","properties":{"name":"korn"}}
     * @return JSON containing the ID of the new node
     */
    public static String create(String query) { return MemoryOperations.create(query); }

    /**
     * Update Nodes
     * @param query Query to link two nodes together. Ex: {"labels":["Person"],"label":"Person","properties":{"name":"davis"},"relationships":{"FROM":[369]},"id":368}
     * @return JSON establishing whether or not the connection was made or not
     */
    public static String update(String query) { return MemoryOperations.update(query); }

    /**
     * Delete a Node
     * @param query JSON query to delete a specified node. Ex: {'type':'node','id':361,'properties_list': ['sex'], 'relationships':{'FRIEND_OF':[426]}}
     * @return Whether or not deleting was successful or not
     */
    //TODO: SDE-60 --> Consistency
    public static String delete(String query) { return MemoryOperations.remove(query); }

}