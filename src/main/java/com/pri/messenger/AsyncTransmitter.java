package com.pri.messenger;


public interface AsyncTransmitter {

    public void asyncSend(Message message) throws RecipientNotFoundException, NetworkException;
}
