/*
 * Created on 01.05.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.messenger.server;

import com.pri.messenger.Messenger;
import com.pri.messenger.NetworkMessenger;
import com.pri.session.ClientSession;

//import com.pri.shex.backend.HttpConnection;


/**
 * @author mg
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
public abstract class ServerNetworkMessenger extends NetworkMessenger {

    /**
     * @param msgr
     */
    public ServerNetworkMessenger(Messenger msgr) {
        super(msgr);
    }

    public abstract void processRequest(HttpConnection conn, ClientSession clData);

}
