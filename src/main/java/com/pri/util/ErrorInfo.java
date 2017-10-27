package com.pri.util;

public interface ErrorInfo {

    public final static int _ERROR_BEGIN = 0;
    public final static int _ERROR_END = 0;

    void setErrorCode(int errcode);

    int getErrorCode();

    void setAuxInfo(Object aux);

    Object getAuxInfo();
}
