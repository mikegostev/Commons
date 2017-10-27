package com.pri.messenger;

import com.pri.messenger.ConnectionStateListener.State;

public abstract class ClientNetworkMessenger extends NetworkMessenger {

    public ClientNetworkMessenger(Messenger msgr) {
        super(msgr);
    }

    abstract public boolean setConnected(boolean st) throws NetworkException;

    abstract public void addConnectionStateListenerListener(ConnectionStateListener l);

    abstract public void removeConnectionStateListenerListener(ConnectionStateListener l);

    abstract public State getState();

}
