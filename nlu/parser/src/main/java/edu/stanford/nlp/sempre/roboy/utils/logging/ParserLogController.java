package edu.stanford.nlp.sempre.roboy.utils.logging;


public class ParserLogController {
    private static boolean ALL = true;
    private static boolean WARN = true;

    public static void setLogger(String s){
        if(s.toLowerCase().equals("off")) {
            ALL =false;
            WARN=false;
            return;
        }
        if(s.toLowerCase().equals("warn")) {
            ALL =false;
            WARN=true ;
            return;
        }

        if(!s.toLowerCase().equals("all")){
            LogInfoToggle.warning("Invalid Level Passed to NLU:\t"+s);
            LogInfoToggle.warning("Please Check Dialog's Config.Properties. Now Defaulting to ALL");
        }
        ALL =true;
        WARN=true;
        return;


    }

    public static boolean isWARN() {
        return WARN;
    }

    public static boolean isALL() {
        return ALL;
    }
}
