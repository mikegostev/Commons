package com.pri.messenger;


public interface SyncSupplier {

    public Message syncReceive(Address address);

    public Message syncReceive(Address address, long timeout) throws SyncReceiveTimeoutException;
}
