/*
 * Created on 23.04.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.messenger;

import com.pri.adob.ADOBFactory;
import com.pri.messenger.ConnectionStateListener.State;
import com.pri.util.ProgressListener;
import java.net.URL;


/**
 * @author mg
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
public class ClientMessenger extends Messenger {

    Messenger localMessenger;
    ClientNetworkMessenger networkMessenger;

    public ClientMessenger(URL sPeer) throws Exception {
        localMessenger = new ThreadPoolDelivery();
        networkMessenger = new NetworkMessengerHTTP(localMessenger, sPeer);

        if (defaultMessenger == null) {
            defaultMessenger = this;
        }
    }

    /* (non-Javadoc)
     * @see com.pri.messenger.SyncTransmitter#syncSend(com.pri.messenger.Message)
     */
    public void syncSend(Message message) throws NetworkException, RecipientNotFoundException {
        syncSend(message, (ProgressListener) null);
    }

    public void syncSend(Message message, ProgressListener pLsnr) throws RecipientNotFoundException, NetworkException {
        if (message.getAddress().isLocal()) {
            localMessenger.syncSend(message, pLsnr);
        } else {
            networkMessenger.syncSend(message, pLsnr);
        }
    }

    public void syncSend(Message message, ADOBFactory af) throws RecipientNotFoundException, NetworkException {
        if (message.getAddress().isLocal()) {
            localMessenger.syncSend(message, af);
        } else {
            networkMessenger.syncSend(message, af);
        }
    }

    public void syncSend(Message message, ADOBFactory af, ProgressListener pLsnr)
            throws RecipientNotFoundException, NetworkException {
        if (message.getAddress().isLocal()) {
            localMessenger.syncSend(message, af, pLsnr);
        } else {
            networkMessenger.syncSend(message, af, pLsnr);
        }
    }

    /* (non-Javadoc)
     * @see com.pri.messenger.AsyncTransmitter#asyncSend(com.pri.messenger.Message)
     */
    public void asyncSend(Message message) throws RecipientNotFoundException, NetworkException {
        if (message.getAddress().isLocal()) {
            localMessenger.asyncSend(message);
        } else {
            networkMessenger.asyncSend(message);
        }
    }

    /* (non-Javadoc)
     * @see com.pri.messenger.SyncSupplier#syncReceive(com.pri.messenger.Address, long)
     */
    public Message syncReceive(Address address) {
        return localMessenger.syncReceive(address);
    }

    public Message syncReceive(Address address, long timeout) throws SyncReceiveTimeoutException {
        return localMessenger.syncReceive(address, timeout);
    }

    /* (non-Javadoc)
     * @see com.pri.messenger.AsyncSupplier#addRecipient(com.pri.messenger.MessageRecipient, com.pri.messenger.Address)
     */
    public void addRecipient(MessageRecipient consumer, Address address) {
        localMessenger.addRecipient(consumer, address);
    }

    /* (non-Javadoc)
     * @see com.pri.messenger.AsyncSupplier#removeRecipient(com.pri.messenger.MessageRecipient, com.pri.messenger
     * .Address)
     */
    public void removeRecipient(MessageRecipient consumer, Address address) {
        localMessenger.removeRecipient(consumer, address);
    }


    public Address getNetworkAddress() {
        return networkMessenger.getNetworkAddress();
    }

    @Override
    public void destroy() {
        localMessenger.destroy();
        networkMessenger.destroy();
    }

    public void setConnected(boolean st) throws NetworkException {
        networkMessenger.setConnected(st);
    }

    public State getState() {
        return networkMessenger.getState();
    }

    public void addConnectionStateListener(ConnectionStateListener l) {
        networkMessenger.addConnectionStateListenerListener(l);
    }

    public void removeConnectionStateListener(ConnectionStateListener l) {
        networkMessenger.removeConnectionStateListenerListener(l);
    }


}
