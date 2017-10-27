package com.pri.log;

import java.io.IOException;
import java.io.Serializable;

public class LogRecord implements Serializable {

    private String loggerName;
    private String className;
    private String method;
    private String fileName;
    private int fileLine;
    private String message;
    private Level level;
    private Object[] params;
    private Throwable thrown;

    private static final long serialVersionUID = 42L;

    public LogRecord() {
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getFileLine() {
        return fileLine;
    }

    public void setFileLine(int fileLine) {
        this.fileLine = fileLine;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public String getLoggerName() {
        return loggerName;
    }

    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public Throwable getThrown() {
        return thrown;
    }

    public void setThrown(Throwable thrown) {
        this.thrown = thrown;
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        Object[] ap = null;

        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                if (!(params[i] instanceof Serializable)) {
                    if (ap == null) {
                        ap = new Object[params.length];
                        for (int j = 0; j < params.length; j++) {
                            ap[j] = params[j];
                        }
                    }

                    params[i] = null;
                }
            }

            out.defaultWriteObject();

            if (ap != null) {
                params = ap;
            }
        }
    }
}
