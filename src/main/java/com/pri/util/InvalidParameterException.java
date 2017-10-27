package com.pri.util;

public class InvalidParameterException extends Exception {

    private Object auxInfo;

    public InvalidParameterException() {
        super();
        // TODO Auto-generated constructor stub
    }

    public InvalidParameterException(String message, Object inf) {
        super(message);
        auxInfo = inf;
    }

    public InvalidParameterException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public InvalidParameterException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public Object getAuxInfo() {
        return auxInfo;
    }

    public void setAuxInfo(Object inf) {
        auxInfo = inf;
    }
}
