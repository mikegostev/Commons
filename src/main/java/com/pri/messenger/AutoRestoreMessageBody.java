package com.pri.messenger;

public interface AutoRestoreMessageBody extends XMLableMessageBody {

    boolean restore(String xmlText);
}
