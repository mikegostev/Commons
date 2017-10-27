package com.pri.messenger;


interface XMLableMessageBody extends MessageBody {

    public abstract String toXML();

// public boolean restore(String xml);
// public LightSAXParser getParser();

}
