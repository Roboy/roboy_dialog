package edu.stanford.nlp.sempre.roboy.utils.logging;

import fig.basic.LogInfo;

import java.io.PrintWriter;

public class LogInfoToggle extends LogInfo {


    public static void begin_track(String format, Object... args) {
        if(ParserLogController.isALL())LogInfo.begin_track(format, args);
    }

    public static void begin_track_printAll(String format, Object... args) {
        if(ParserLogController.isALL())LogInfo.begin_track_printAll(format, args);
    }

    public static void begin_track_general(Object o, boolean printAllChildLines, boolean printIfParentPrinted) {
        if(ParserLogController.isALL())LogInfo.begin_track_general(o, printAllChildLines, printIfParentPrinted);
    }

    public static void end_track() {
        if(ParserLogController.isALL())LogInfo.end_track();
    }

    public static <T> T end_track(T x) {
        if(ParserLogController.isALL()) return LogInfo.end_track(x);
        return null;
    }

    public static void log(Object o) {
        if(ParserLogController.isALL())LogInfo.log(o);
    }

    public static void logs(String format, Object... args) {
        if(ParserLogController.isALL())LogInfo.logs(format,args);
    }

    public static void setFileOut(PrintWriter newFileOut) {
        if(ParserLogController.isALL()) LogInfo.setFileOut(newFileOut);
    }



    public static void dbgs(String format, Object... args) {
        if(ParserLogController.isALL()) LogInfo.dbgs(format, args);
    }

    public static void dbg(Object o) {

        if(ParserLogController.isALL()) LogInfo.dbg(o);
    }

    public static void error(Object o) {
//        if(WARN)
        LogInfo.error(o);
    }

    public static void errors(String format, Object... args) {
//        if(WARN)
        LogInfo.errors(format,args);
    }

    public static void warnings(String format, Object... args) {
        if(ParserLogController.isWARN()) LogInfo.warnings(format, args);
    }

    public static void warning(Object o) {
        if(ParserLogController.isWARN()) LogInfo.warning(o);
    }

    public static void fails(String format, Object... args) {
//        if(WARN)
        LogInfo.fails(format, args);
    }

    public static void fail(Object o) {
//        if(WARN)
        LogInfo.fail(o);
    }




}
