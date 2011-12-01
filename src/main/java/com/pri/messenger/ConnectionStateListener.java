package com.pri.messenger;

public interface ConnectionStateListener
{
 static enum State {CONNECTED,NETWORK_ERROR,SESSION_EXPIRED,SESSION_CLOSED,DISCONNECTED};
 void stateStateChanged( State st );
}
