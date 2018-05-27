package roboy.ros;

import org.roboy.memory.ros.ServiceReplacement;

public class RoslessCalls {

    public static String get   (String query) { return ServiceReplacement.getServiceHandler(query);   }
    public static String cypher(String query){
        return ServiceReplacement.cyperServiceHandler(query);
    }
    public static String create(String query) { return ServiceReplacement.createServiceHandler(query);}
    public static String update(String query) { return ServiceReplacement.updateServiceHandler(query);}
    public static String delete(String query) { return ServiceReplacement.deleteServiceHandler(query);}

}
