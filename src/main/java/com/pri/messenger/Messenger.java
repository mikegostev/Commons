package com.pri.messenger;


public abstract class Messenger implements SyncTransmitter, AsyncTransmitter, SyncSupplier, AsyncSupplier {

    protected static Messenger defaultMessenger;

    static public Messenger getDefaultMessenger() {
        return defaultMessenger;
    }

    public abstract void destroy();

}
