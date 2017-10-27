package com.pri.log;

public abstract class Logger {

    public static Logger getLogger(String logger) {
        return LogFactory.getLogger(logger);
    }

    public static Logger getLogger(Class logger) {
        return LogFactory.getLogger(logger);
    }

    public abstract boolean isDebugEnabled();

    public abstract boolean isErrorEnabled();

    public abstract boolean isFatalEnabled();

    public abstract boolean isInfoEnabled();

    public abstract boolean isTraceEnabled();

    public abstract boolean isWarnEnabled();


    public abstract void trace(String message, Object... param);

    public abstract void trace(String message, Throwable t, Object... param);

    public abstract void debug(String message, Object... param);

    public abstract void debug(String message, Throwable t, Object... param);

    public abstract void info(String message, Object... param);

    public abstract void info(String message, Throwable t, Object... param);

    public abstract void warn(String message, Object... param);

    public abstract void warn(String message, Throwable t, Object... param);

    public abstract void error(String message, Object... param);

    public abstract void error(String message, Throwable t, Object... param);

    public abstract void fatal(String message, Object... param);

    public abstract void fatal(String message, Throwable t, Object... param);

    public abstract void trace(StackTraceElement caller, String message, Object... param);

    public abstract void trace(StackTraceElement caller, String message, Throwable t, Object... param);

    public abstract void debug(StackTraceElement caller, String message, Object... param);

    public abstract void debug(StackTraceElement caller, String message, Throwable t, Object... param);

    public abstract void info(StackTraceElement caller, String message, Object... param);

    public abstract void info(StackTraceElement caller, String message, Throwable t, Object... param);

    public abstract void warn(StackTraceElement caller, String message, Object... param);

    public abstract void warn(StackTraceElement caller, String message, Throwable t, Object... param);

    public abstract void error(StackTraceElement caller, String message, Object... param);

    public abstract void error(StackTraceElement caller, String message, Throwable t, Object... param);

    public abstract void fatal(StackTraceElement caller, String message, Object... param);

    public abstract void fatal(StackTraceElement caller, String message, Throwable t, Object... param);

}
