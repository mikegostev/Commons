package com.pri.log;

public class GLog {

    private GLog() {
    }

    public static Logger getLogger(String logger) {
        return LogFactory.getLogger(logger);
    }

    public static Logger getLogger(Class logger) {
        return LogFactory.getLogger(logger);
    }

    public static void trace(String message, Object... param) {
        LogFactory.getLogger("global").trace(message, param);
    }

    public static void trace(String message, Throwable t, Object... param) {
        LogFactory.getLogger("global").trace(message, t, param);
    }

    public static void debug(String message, Object... param) {
        LogFactory.getLogger("global").debug(message, param);
    }

    public static void debug(String message, Throwable t, Object... param) {
        LogFactory.getLogger("global").debug(message, t, param);
    }

    public static void info(String message, Object... param) {
        LogFactory.getLogger("global").info(message, param);
    }

    public static void info(String message, Throwable t, Object... param) {
        LogFactory.getLogger("global").info(message, t, param);
    }

    public static void warn(String message, Object... param) {
        LogFactory.getLogger("global").warn(message, param);
    }

    public static void warn(String message, Throwable t, Object... param) {
        LogFactory.getLogger("global").warn(message, t, param);
    }

    public static void error(String message, Object... param) {
        LogFactory.getLogger("global").error(message, param);
    }

    public static void error(String message, Throwable t, Object... param) {
        LogFactory.getLogger("global").error(message, t, param);
    }

    public static void fatal(String message, Object... param) {
        LogFactory.getLogger("global").fatal(message, param);
    }

    public static void fatal(String message, Throwable t, Object... param) {
        LogFactory.getLogger("global").fatal(message, t, param);
    }
}
