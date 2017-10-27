/*
 * Created on 03.05.2004
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
public class SyncReceiveTimeoutException extends MessageDeliveryException {

    public SyncReceiveTimeoutException(long timeout) {
        super("Receive operation timeouted: " + timeout + "ms");
    }

}
