/*
 * Created on 20.04.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.messenger;


/**
 * @author mg
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
public abstract class NetworkMessenger implements AsyncTransmitter, SyncTransmitter {

    protected Messenger localMessenger;
    private Address networkAddress;

    public NetworkMessenger(Messenger msgr) {
        localMessenger = msgr;
    }

    /**
     * @return Returns the networkAddress.
     */
    public Address getNetworkAddress() {
        return networkAddress;
    }

    /**
     * @param networkAddress The networkAddress to set.
     */
    public void setNetworkAddress(Address networkAddress) {
        this.networkAddress = networkAddress;
    }

    public abstract void destroy();

}
