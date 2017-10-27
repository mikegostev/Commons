package com.pri.messenger;

import com.pri.adob.ADOBFactory;
import com.pri.util.ProgressListener;


public interface SyncTransmitter {

    public void syncSend(Message message) throws NetworkException, RecipientNotFoundException;

    public void syncSend(Message message, ProgressListener pl) throws RecipientNotFoundException, NetworkException;

    public void syncSend(Message message, ADOBFactory af) throws RecipientNotFoundException, NetworkException;

    public void syncSend(Message message, ADOBFactory af, ProgressListener pl)
            throws RecipientNotFoundException, NetworkException;
}
