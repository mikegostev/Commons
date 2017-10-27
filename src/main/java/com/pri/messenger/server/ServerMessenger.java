/*
 * Created on 30.04.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.messenger.server;

import com.pri.adob.ADOBFactory;
import com.pri.messenger.Address;
import com.pri.messenger.Message;
import com.pri.messenger.MessageRecipient;
import com.pri.messenger.Messenger;
import com.pri.messenger.NetworkException;
import com.pri.messenger.RecipientNotFoundException;
import com.pri.messenger.SyncReceiveTimeoutException;
import com.pri.messenger.ThreadPoolDelivery;
import com.pri.session.ClientSession;
import com.pri.session.SessionManager;
import com.pri.util.ProgressListener;
import java.util.concurrent.ExecutorService;

/**
 * @author mg
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
public class ServerMessenger extends Messenger {

    Messenger localMessenger;
    ClientServerMessenger networkMessenger;

    public ServerMessenger(SessionManager smngr) throws Exception {
        localMessenger = new ThreadPoolDelivery();
        networkMessenger = new ClientServerMessenger(localMessenger, smngr);

        if (defaultMessenger == null) {
            defaultMessenger = this;
        }
    }

    public ServerMessenger(Messenger localMsgr, SessionManager smngr) throws Exception {
        localMessenger = localMsgr;
        networkMessenger = new ClientServerMessenger(localMessenger, smngr);

        if (defaultMessenger == null) {
            defaultMessenger = this;
        }
    }

    public ServerMessenger(ExecutorService executorService, SessionManager smngr) throws Exception {
        this(null, executorService, smngr);
    }

    public ServerMessenger(Messenger localMsgr, ExecutorService executorService, SessionManager smngr) {
        if (localMsgr == null) {
            if (executorService != null) {
                localMessenger = new ThreadPoolDelivery(executorService);
            } else {
                localMessenger = new ThreadPoolDelivery();
            }
        } else {
            localMessenger = localMsgr;
        }

        networkMessenger = new ClientServerMessenger(localMessenger, smngr);

        if (defaultMessenger == null) {
            defaultMessenger = this;
        }
    }

    /* (non-Javadoc)
     * @see com.pri.messenger.SyncTransmitter#syncSend(com.pri.messenger.Message)
     */
    public void syncSend(Message message) throws RecipientNotFoundException, NetworkException {
        syncSend(message, (ProgressListener) null);
    }

    public void syncSend(Message message, ProgressListener pLsnr) throws RecipientNotFoundException, NetworkException {
        if (message.getAddress().isLocal()) {
            localMessenger.syncSend(message);
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

    public Message syncReceive(Address address) {
        return localMessenger.syncReceive(address);
    }


    /* (non-Javadoc)
     * @see com.pri.messenger.SyncSupplier#syncReceive(com.pri.messenger.Address, long)
     */
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

    public void processRequest(HttpConnection conn, ClientSession cld) {
        networkMessenger.processRequest(conn, cld);
    }

    public Config getConfig() {
        return networkMessenger.getConfig();
    }

    @Override
    public void destroy() {
        localMessenger.destroy();
        networkMessenger.destroy();
    }

}
