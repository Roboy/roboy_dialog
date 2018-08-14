package edu.stanford.nlp.sempre.roboy.utils.logging;


import org.apache.logging.log4j.Level;

public class ParserLogController {
    private static boolean ALL = true;
    private static boolean WARN = true;
    private static Level level = Level.INFO;

    @Deprecated
    /**
     * Set Logger to either ALL, WARN or OFF modes
     *
     * ALL: All messages + Warnings
     * WARN: !ALL and Warnings
     * OFF: Nothing shown except fails and warnings
     *
     * Things that are classified as fails and warnings shall always be displayed, even when off
     *
     * @param s String representing the Logger mode
     */
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

    public static void setLogger(Level pLevel){
        if(pLevel!=null)level = pLevel;
    }


    public static boolean isWARN() {
        return WARN;
    }

    public static boolean isALL() {
        return ALL;
    }

    public static Level getLevel() {
        return level;
    }
}
