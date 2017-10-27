package com.pri.log;

public class Log {

    private Log() {
    }

    private static boolean loggerPerClass = false;

    public static boolean isLoggerPerClass() {
        return loggerPerClass;
    }

    public static void setLoggerPerClass(boolean loggerPerClass) {
        Log.loggerPerClass = loggerPerClass;
    }

    public static Logger getLogger(String logger) {
        return LogFactory.getLogger(logger);
    }

    public static Logger getLogger(Class logger) {
        if (loggerPerClass) {
            return LogFactory.getLogger(logger);
        }

//  System.out.println( "Class name: "+  logger.getName()+" class package: "+logger.getPackage());

        String clName = logger.getName();

        int pos = clName.lastIndexOf('.');

        String pkgName = pos > 0 ? clName.substring(0, clName.lastIndexOf('.')) : "";

        return LogFactory.getLogger(pkgName);
    }

    public static Logger getLogger() {
        StackTraceElement caller = new Throwable().getStackTrace()[1];
        String cname = caller.getClassName();
        if (loggerPerClass) {
            return LogFactory.getLogger(cname);
        }

        int pos = cname.lastIndexOf('.');
        if (pos > 0) {
            return LogFactory.getLogger(cname.substring(0, pos));
        }

        return LogFactory.getLogger("");
    }

    public static void trace(String message, Object... param) {
        StackTraceElement caller = new Throwable().getStackTrace()[1];
        LogFactory.getLogger(caller.getClassName()).trace(caller, message, param);
    }

    public static void trace(String message, Throwable t, Object... param) {
        StackTraceElement caller = new Throwable().getStackTrace()[1];
        LogFactory.getLogger(caller.getClassName()).trace(caller, message, t, param);
    }

    public static void debug(String message, Object... param) {
        StackTraceElement caller = new Throwable().getStackTrace()[1];
        LogFactory.getLogger(caller.getClassName()).debug(caller, message, param);
    }

    public static void debug(String message, Throwable t, Object... param) {
        StackTraceElement caller = new Throwable().getStackTrace()[1];
        LogFactory.getLogger(caller.getClassName()).debug(caller, message, t, param);
    }

    public static void info(String message, Object... param) {
        StackTraceElement caller = new Throwable().getStackTrace()[1];
        LogFactory.getLogger(caller.getClassName()).info(caller, message, param);
    }

    public static void info(String message, Throwable t, Object... param) {
        StackTraceElement caller = new Throwable().getStackTrace()[1];
        LogFactory.getLogger(caller.getClassName()).info(caller, message, t, param);
    }

    public static void warn(String message, Object... param) {
        StackTraceElement caller = new Throwable().getStackTrace()[1];
        LogFactory.getLogger(caller.getClassName()).warn(caller, message, param);
    }

    public static void warn(String message, Throwable t, Object... param) {
        StackTraceElement caller = new Throwable().getStackTrace()[1];
        LogFactory.getLogger(caller.getClassName()).warn(caller, message, t, param);
    }

    public static void error(String message, Object... param) {
        StackTraceElement caller = new Throwable().getStackTrace()[1];
        LogFactory.getLogger(caller.getClassName()).error(caller, message, param);
    }

    public static void error(String message, Throwable t, Object... param) {
        StackTraceElement caller = new Throwable().getStackTrace()[1];
        LogFactory.getLogger(caller.getClassName()).error(caller, message, t, param);
    }

    public static void fatal(String message, Object... param) {
        StackTraceElement caller = new Throwable().getStackTrace()[1];
        LogFactory.getLogger(caller.getClassName()).fatal(caller, message, param);
    }

    public static void fatal(String message, Throwable t, Object... param) {
        StackTraceElement caller = new Throwable().getStackTrace()[1];
        LogFactory.getLogger(caller.getClassName()).fatal(caller, message, t, param);
    }

}
