package edu.stanford.nlp.sempre.roboy.utils.logging;

import fig.basic.LogInfo;
import org.apache.logging.log4j.Level;

import java.io.PrintWriter;

public class LogInfoToggle extends LogInfo {




    public static void begin_track(String format, Object... args) {
        if(ParserLogController.getLevel().isLessSpecificThan(Level.DEBUG))LogInfo.begin_track(format, args);
    }

    public static void begin_track_printAll(String format, Object... args) {
        if(ParserLogController.getLevel().isLessSpecificThan(Level.DEBUG))LogInfo.begin_track_printAll(format, args);
    }

    public static void begin_track_general(Object o, boolean printAllChildLines, boolean printIfParentPrinted) {
        if(ParserLogController.getLevel().isLessSpecificThan(Level.DEBUG))LogInfo.begin_track_general(o, printAllChildLines, printIfParentPrinted);
    }

    public static void end_track() {
        if(ParserLogController.getLevel().isLessSpecificThan(Level.DEBUG))LogInfo.end_track();
    }

    public static <T> T end_track(T x) {
        if(ParserLogController.getLevel().isLessSpecificThan(Level.DEBUG)) return LogInfo.end_track(x);
        return null;
    }

    public static void log(Object o) {
        if(ParserLogController.getLevel().isLessSpecificThan(Level.INFO))LogInfo.log(o);
    }

    public static void logs(String format, Object... args) {
        if(ParserLogController.getLevel().isLessSpecificThan(Level.INFO)) LogInfo.logs(format,args);
    }

    public static void setFileOut(PrintWriter newFileOut) {
//        LogInfo.setFileOut(null);
    }

    public static void dbgs(String format, Object... args) {
        if(ParserLogController.getLevel().isLessSpecificThan(Level.DEBUG)) LogInfo.dbgs(format, args);
    }

    public static void dbg(Object o) {

        if(ParserLogController.getLevel().isLessSpecificThan(Level.DEBUG)) LogInfo.dbg(o);
    }

    public static void error(Object o) {
        if(ParserLogController.getLevel().isLessSpecificThan(Level.ERROR))LogInfo.error(o);
    }

    public static void errors(String format, Object... args) {
        if(ParserLogController.getLevel().isLessSpecificThan(Level.ERROR))LogInfo.errors(format,args);
    }

    public static void warnings(String format, Object... args) {
        if(ParserLogController.getLevel().isLessSpecificThan(Level.WARN)) LogInfo.warnings(format, args);
    }

    public static void warning(Object o) {
        if(ParserLogController.getLevel().isLessSpecificThan(Level.WARN)) LogInfo.warning(o);
    }

    public static void fails(String format, Object... args) {
        if(ParserLogController.getLevel().isLessSpecificThan(Level.FATAL))LogInfo.fails(format, args);
    }

    public static void fail(Object o) {
        if(ParserLogController.getLevel().isLessSpecificThan(Level.FATAL))LogInfo.fail(o);
    }




}
