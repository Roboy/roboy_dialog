package roboy.memory;


import org.roboy.memory.util.MemoryOperations;

public class Neo4jMemoryOperations {

    public static String get   (String query) { return MemoryOperations.get(query);    }
    public static String cypher(String query) { return MemoryOperations.cypher(query); }
    public static String create(String query) { return MemoryOperations.create(query); }
    public static String update(String query) { return MemoryOperations.update(query); }

    //TODO: SDE-60 --> Consistency
    public static String delete(String query) { return MemoryOperations.remove(query); }

}
