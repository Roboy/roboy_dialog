package roboy.memory;


import org.roboy.memory.util.MemoryOperations;

public class Neo4jMemoryOperations {

    /*
    These classes replicate the functionality of the ROS service calls. They parse JSON queries in the format of
    {"labels":["Person"],"label":"Person","properties":{"name":"jason"}}
    Returned are Json statements like {"id":[367]}
     */


    public static String get   (String query) { return MemoryOperations.get(query);    }
    public static String cypher(String query) { return MemoryOperations.cypher(query); }
    public static String create(String query) { return MemoryOperations.create(query); }
    public static String update(String query) { return MemoryOperations.update(query); }

    //TODO: SDE-60 --> Consistency
    public static String delete(String query) { return MemoryOperations.remove(query); }

}
